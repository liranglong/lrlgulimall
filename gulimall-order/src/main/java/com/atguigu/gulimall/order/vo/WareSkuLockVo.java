package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.util.List;

/**
 * 锁定库存的vo
 * 创建订单的时候，封装的数据
 * @author: wan
 */
@Data
public class WareSkuLockVo {
    private String orderSn;
    /** 需要锁住的所有库存信息 **/
    private List<OrderItemVo> locks;
}
