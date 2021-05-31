package com.atguigu.gulimall.product;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class GulimallProductApplicationTests {
//    @Autowired
//    RedissonClient redissonClient;
//
//    @Autowired
//    BrandService brandService;
//
////    @Autowired
////    OSSClient ossClient;
//
//    @Autowired
//    private CategoryService categoryService;
//
//    @Autowired
//    StringRedisTemplate redisTemplate;
//
//    @Autowired
//    AttrGroupDao attrGroupDao;
//
//    @Test
//    void testGetAttrGroupWithAttrsBySpuId() {
//        attrGroupDao.getAttrGroupWithAttrsBySpuId(4L, 225L).forEach(item->
//                System.out.println(item));
//    }
//
//
//    @Test
//    void testRedisson() {
//
//        System.out.println(redissonClient);
//
//    }
//
//    @Test
//    void testRedis() {
//        ValueOperations<String, String> ops = redisTemplate.opsForValue();
//        // 保存
//        ops.set("hello", "world_" + UUID.randomUUID().toString());
//        // 查询
//        String hello = ops.get("hello");
//        System.out.println(hello);
//
//    }
//
//
//
//
//    @Test
//    void testCategoryPath() {
//        log.info("完整路径：{}", Arrays.asList(categoryService.findCatelogId(225L)));
//    }
//
//
//    @Test
//    void testUpload() throws FileNotFoundException {
//        // Endpoint以杭州为例，其它Region请按实际情况填写。
//        String endpoint = "oss-cn-shanghai.aliyuncs.com";
//        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
//        String accessKeyId = "XXX";
//        String accessKeySecret = "XXX";
//
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//        // 上传文件流。
//        InputStream inputStream = new FileInputStream("C:\\Users\\Administrator\\Desktop\\product-mapping.txt");
//        ossClient.putObject("gulimall-wan", "product-mapping.txt", inputStream);
//
//        // 关闭OSSClient。
//        ossClient.shutdown();
//
//        System.out.println("上传成功");
//    }
//
//    @Test
//    void contextLoads() {
//        BrandEntity entity = new BrandEntity();
//        entity.setName("华为");
//        boolean save = brandService.save(entity);
//        System.out.println("保存成功：" + save);
//    }
//
//    @Test
//    void update() {
//        BrandEntity entity = new BrandEntity();
//        entity.setBrandId(1L);
//        entity.setName("小米");
//        boolean save = brandService.updateById(entity);
//        System.out.println("xiugai成功：" + save);
//    }
//
//    @Test
//    void queryPage() {
//        //brandService.queryPage()
//        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
//        list.forEach((item) -> {
//            System.out.println(item);
//        });
//    }

}
