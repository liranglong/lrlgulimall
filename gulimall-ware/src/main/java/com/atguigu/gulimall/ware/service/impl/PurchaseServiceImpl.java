package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.constant.WareConstant;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.ware.dao.PurchaseDao;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;
import com.atguigu.gulimall.ware.service.PurchaseDetailService;
import com.atguigu.gulimall.ware.service.PurchaseService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.atguigu.gulimall.ware.vo.MergeVo;
import com.atguigu.gulimall.ware.vo.PurchaseDoneVo;
import com.atguigu.gulimall.ware.vo.PurchaseItemDoneVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;
    @Autowired
    WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询未领取的采购单
     */
    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 0).or().eq("status", 1);

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 04、合并采购需求
     * @param mergeVo
     */
    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        // TODO 采购需求的状态必须是 新建、已分配 才可以合并
        boolean isMerge = true;
        List<Long> items = mergeVo.getItems();
        if (!CollectionUtils.isEmpty(items)) {
            List<PurchaseDetailEntity> byIds = purchaseDetailService.listByIds(items);
            for (int i = 0; i < byIds.size(); i++) {
                if (byIds.get(i).getStatus() != WareConstant.PurchaseDetailStatusEnum.CREATED.getCode() &&
                        byIds.get(i).getStatus() != WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode()) {
                    isMerge = false;
                    break;
                }
            }
        }else {
            isMerge = false;
        }
        if (isMerge) {
            Long purchaseId = mergeVo.getPurchaseId();
            if (purchaseId == null) {
                // 1、新建一个采购单
                PurchaseEntity purchaseEntity = new PurchaseEntity();
                purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
                purchaseEntity.setCreateTime(new Date());
                purchaseEntity.setUpdateTime(new Date());
                this.save(purchaseEntity);
                purchaseId = purchaseEntity.getId();
            }
            items = mergeVo.getItems();
            // 2、修改采购需求，将采购单purchaseId加进去
            Long finalPurchaseId = purchaseId;
            List<PurchaseDetailEntity> collect = items.stream().map(i -> {
                PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();

                detailEntity.setId(i);
                detailEntity.setPurchaseId(finalPurchaseId);
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                return detailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect);

            // 修改更新时间
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(purchaseId);
            purchaseEntity.setUpdateTime(new Date());
            this.updateById(purchaseEntity);
        }
    }

    /**
     * 06、领取采购单
     * 不考虑细节：只能是自己的采购单
     * @param ids 采购单ID
     */
    @Transactional
    @Override
    public void received(List<Long> ids) {
        // 1、修改所有采购单的状态为 "已领取"
        // 1.1）过滤采购单【必须是新建或者已分配的采购单】，保存采购单修改时间
        List<PurchaseEntity> collect = ids.stream().map(id -> {
            PurchaseEntity purchaseEntity = this.getById(id);
            return purchaseEntity;
        }).filter(item -> {
            if (item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                    item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
                return true;
            }
            return false;
        }).map(item -> {
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());
        // 1.2、改变采购单的状态
        this.updateBatchById(collect);

        // 2、修改采购需求为 "正在购买"【采购单关联的采购需求】
        // collect是采购单集合
        collect.forEach(item->{
            // 根据采购单id列出 采购需求信息
            List <PurchaseDetailEntity> entities = purchaseDetailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> purchaseDetailEntities = entities.stream().map(entity -> {
                // 为什么要重新new一个对象？
                // 因为只要修改指定的字段
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(entity.getId());
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(purchaseDetailEntities);
        });
    }

    /**
     * 完成采购
     */
    @Transactional
    @Override
    public void done(PurchaseDoneVo doneVo) {
        // 2、改变采购需求的状态
        Boolean flag = true;
        List<PurchaseItemDoneVo> items = doneVo.getItems();
        List<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity detailEntity= new PurchaseDetailEntity();
            // 设置状态：成功或失败
            detailEntity.setStatus(item.getStatus());
            // 优化，下面只需要执行一次
            if (flag && item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()) {
                flag = false;
            }else {
                // 3、将成功采购的进行入库 [三个参数：sku_id，ware_id，stock]
                // 根据采购需求id获取采购需求详情，获得sku_id
                PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
                // 成功的采购需求，把库存加上去【这里要设置下锁定库存的数量为0，如果是新增库存记录】
                // 这里并不修改整个entity字段，只修改这三个字段是对的
                wareSkuService.addStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum());
            }
            // TODO 采购失败待完善，应采购数量10，实际采购数量8
            // 采购需求的状态【可能是成功，或失败，采购员提交】
            detailEntity.setId(item.getItemId());
            updates.add(detailEntity);
        }
        purchaseDetailService.updateBatchById(updates);

        // 1、改变采购单状态【如果存在失败的采购项(采购需求)，采购单状态异常】
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(doneVo.getId());
        purchaseEntity.setStatus(flag ? WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        this.updateById(purchaseEntity);
    }
}