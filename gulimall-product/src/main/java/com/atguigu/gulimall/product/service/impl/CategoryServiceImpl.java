package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catalog2Vo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redisson;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 1、查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        // 2、组装成父子的属性结构
        // 2.1)找到所有的一级分类
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map(menu -> {
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2) ->
                (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort())
        ).collect(Collectors.toList());

        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1、检查当前删除的菜单，是否被别的地方引用

        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    // [2,25,225]

    /**
     * 级联更新所有关联的数据
     *
     * @CacheEvict：失效模式
     */
//    @Caching(evict = {
//            @CacheEvict(value = {"category"}, key = "'getLevel1Categorys'"),
//            @CacheEvict(value = {"category"}, key = "'getCatalogJson'")
//    })
    @CacheEvict(value = {"category"},allEntries = true)
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        if (!StringUtils.isEmpty(category.getName())) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());

            // TODO 更新其他关联
        }
    }

    @Override
    public Long[] findCatelogId(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        findParentPath(catelogId, paths);// 递归查询父节点
        Collections.reverse(paths);// 逆序
        return paths.toArray(new Long[0]);
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        // 1、收集父ID
        paths.add(catelogId);
        // 查库
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    // 递归给每一个menu设置 children
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(menu ->
                menu.getParentCid().equals(root.getCatId())
        ).map(menu -> {
            menu.setChildren(getChildrens(menu, all));
            return menu;
        }).sorted((menu1, menu2) ->
                (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort())
        ).collect(Collectors.toList());
        return children;
    }

    /**
     * 查询一级分类。
     * 父ID是0， 或者  层级是1
     */
    @Cacheable(value = {"category"}, key = "#root.method.name", sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        System.out.println("调用了 getLevel1Categorys  查询了数据库........【一级分类】");
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    // TODO 产生堆外内存溢出：OutOfDirectMemoryError
    // 1）springboot2.0以后默认使用lettuce作为操作redis的客户端。他使用netty进行网络通信
    // 2）lettuce的bug导致netty堆外内存溢出 -Xmx1024m；netty如果没有指定堆外内存，默认使用-Xmx1024m，跟jvm设置的一样【迟早会出异常】
    //  可以通过-Dio.netty.maxDirectMemory进行设置【仍然会异常】
    //  解决方案：不能使用-Dio.netty.maxDirectMemory
    //  1）升级lettuce客户端；【2.3.2已解决】【lettuce使用netty吞吐量很大】
    //  2）切换使用jedis客户端【这里学习一下如何使用jedis，但是最后不选用】

    /**
     * 使用Spring Cache简化 缓存存取操作【不需要自己调用redistemplate客户端来存取数据了，直接加注解】
     * 【但是读写锁还是需要redisson配合使用】
     */
    @Cacheable(value = "category", key = "#root.methodName")
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        // 一次性获取所有 数据
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        System.out.println("调用了 getCatalogJson  查询了数据库........【三级分类】");
        // 1）、所有1级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        // 2）、封装数据
        Map<String, List<Catalog2Vo>> collect = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), level1 -> {
            // 查到当前1级分类的2级分类
            List<CategoryEntity> category2level = getParent_cid(selectList, level1.getCatId());
            List<Catalog2Vo> catalog2Vos = null;
            if (category2level != null) {
                catalog2Vos = category2level.stream().map(level12 -> {
                    // 查询当前2级分类的3级分类
                    List<CategoryEntity> category3level = getParent_cid(selectList, level12.getCatId());
                    List<Catalog2Vo.Catalog3Vo> catalog3Vos = null;
                    if (category3level != null) {
                        catalog3Vos = category3level.stream().map(level13 -> {
                            return new Catalog2Vo.Catalog3Vo(level12.getCatId().toString(), level13.getCatId().toString(), level13.getName());
                        }).collect(Collectors.toList());
                    }
                    return new Catalog2Vo(level1.getCatId().toString(), catalog3Vos, level12.getCatId().toString(), level12.getName());
                }).collect(Collectors.toList());
            }
            return catalog2Vos;
        }));
        return collect;
    }

    /**
     * 带缓存功能：使用redisson完成分布式锁操作，封装了lua 脚本代码的版本，往redis存入一个Hash，实现了读写锁
     * 这里是自己调用redistemplate客户端存取缓存，SpringCache可以使用注解直接决定方式是否调用，返回值直接存入缓存
     * 查询三级分类
     * 缓存穿透：设置null
     * 缓存雪崩：设置不同的过期时间
     * 缓存击穿：加分布式锁
     */
    public Map<String, List<Catalog2Vo>> getCatalogJson2() {
        Map<String, List<Catalog2Vo>> catalogJsonFromDb = null;
        // 1、加入缓存逻辑
        String catlogJSON = redisTemplate.opsForValue().get("catlogJSON");
        if (StringUtils.isEmpty(catlogJSON)) {
            // 2、缓存中没有，查询数据库
            catalogJsonFromDb = getCatalogJsonFromDbWithRedisLock();
            // 3、数据查询结束放入缓存，但是要在同步代码块中放入缓存，否则有可能线程1还没放入缓存导致线程2重新查询一次
            return catalogJsonFromDb;
        } else {
            // 解析json
            Map<String, List<Catalog2Vo>> map = JSON.parseObject(catlogJSON, new TypeReference<Map<String, List<Catalog2Vo>>>() {
            });
            return map;
        }
    }

    /**
     * 从数据库查询并封装分类数据
     * 查询三级分类
     * 分布式锁：redisson
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedissonLock() {
        // 1、锁的名字。锁的粒度：越细越快
        // 例：具体缓存的是某个数据，11号商品，product
        RLock lock = redisson.getLock("catalogJson-lock");
        lock.lock();

        Map<String, List<Catalog2Vo>> dataFromDB = null;
        try {
            // 加锁成功....执行业务【内部会再判断一次redis是否有值】
            dataFromDB = getDataFromDB();
        } finally {
            // 2、查询UUID是否是自己，是自己的lock就删除
            // 查询+删除 必须是原子操作：lua脚本解锁
            lock.unlock();
        }
        return dataFromDB;
    }

    /**
     * 从数据库查询并封装分类数据
     * 查询三级分类
     * 分布式锁：自己实现的
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedisLock() {
        // 1、占本分布式锁。去redis占坑，同时设置过期时间
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            // 加锁成功....执行业务【内部会判断一次redis是否有值】
            Map<String, List<Catalog2Vo>> dataFromDB = null;
            try {
                dataFromDB = getDataFromDB();
            } finally {
                // 2、查询UUID是否是自己，是自己的lock就删除
                // 查询+删除 必须是原子操作：lua脚本解锁
                String luaScript = "if redis.call('get',KEYS[1]) == ARGV[1]\n" +
                        "then\n" +
                        "    return redis.call('del',KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                // 删除锁
                Long lock1 = redisTemplate.execute(new DefaultRedisScript<Long>(luaScript, Long.class),
                        Arrays.asList("lock"), uuid);
            }
            return dataFromDB;
        } else {
            // 加锁失败....重试
            // 休眠100ms重试
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatalogJsonFromDbWithRedisLock();// 自旋的方式
        }
    }

    /**
     * 从数据库查询，并封装缓存
     * 逻辑优化：一次性查出数据，而不是多次查询数据库。
     */
    private Map<String, List<Catalog2Vo>> getDataFromDB() {
        String catlogJSON = redisTemplate.opsForValue().get("catlogJSON");
        // double check，拿到锁了还要判断下是否有数据
        if (!StringUtils.isEmpty(catlogJSON)) {
            return JSON.parseObject(catlogJSON, new TypeReference<Map<String, List<Catalog2Vo>>>() {
            });
        }

        // 一次性获取所有 数据
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        System.out.println("查询了数据库........");
        // 1）、所有1级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        // 2）、封装数据
        Map<String, List<Catalog2Vo>> collect = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), level1 -> {
            // 查到当前1级分类的2级分类
            List<CategoryEntity> category2level = getParent_cid(selectList, level1.getCatId());
            List<Catalog2Vo> catalog2Vos = null;
            if (category2level != null) {
                catalog2Vos = category2level.stream().map(level12 -> {
                    // 查询当前2级分类的3级分类
                    List<CategoryEntity> category3level = getParent_cid(selectList, level12.getCatId());
                    List<Catalog2Vo.Catalog3Vo> catalog3Vos = null;
                    if (category3level != null) {
                        catalog3Vos = category3level.stream().map(level13 -> {
                            return new Catalog2Vo.Catalog3Vo(level12.getCatId().toString(), level13.getCatId().toString(), level13.getName());
                        }).collect(Collectors.toList());
                    }
                    return new Catalog2Vo(level1.getCatId().toString(), catalog3Vos, level12.getCatId().toString(), level12.getName());
                }).collect(Collectors.toList());
            }
            return catalog2Vos;
        }));

        // 3）、查到的数据放入缓存，将对象转成JSON串放入缓存
        redisTemplate.opsForValue().set("catlogJSON", JSON.toJSONString(collect), 1, TimeUnit.DAYS);
        return collect;
    }

    /**
     * 从数据库查询并封装分类数据
     * 查询三级分类
     * 本地锁
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithLocalLock() {
        // TODO 本地锁：synchronized，JUC(lock)，在分布式情况下，需要使用分布式锁
        synchronized (this) {
            // 得到锁以后还要检查一次，double check
            return getDataFromDB();
        }
    }

    /**
     * 查询出父ID为 parent_cid的List集合
     */
    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        return selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        //return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", level.getCatId()));
    }
}