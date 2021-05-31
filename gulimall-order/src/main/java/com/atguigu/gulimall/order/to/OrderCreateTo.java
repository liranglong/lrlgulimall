package com.atguigu.gulimall.order.to;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 提交订单接口：创建的订单To对象
 * 1、订单
 * 2、订单项
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: wan
 */
@Data
public class OrderCreateTo {
    private OrderEntity order;  // 订单
    private List<OrderItemEntity> orderItems; // 订单项
    /** 订单计算的应付价格 **/
    private BigDecimal payPrice;
    /** 运费 **/
    private BigDecimal fare;
}
