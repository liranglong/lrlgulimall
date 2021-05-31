package com.atguigu.gulimall.order.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author: wan
 * 订单确认页需要用的数据
 */
public class OrderConfirmVo {

    @Getter @Setter
    List<MemberAddressVo> memberAddressVos;/** 会员收获地址列表 **/
    @Getter @Setter
    List<OrderItemVo> items;    /** 所有选中的购物项【购物车中的所有项】 **/
    @Getter @Setter
    private Integer integration;/** 优惠券（会员积分） **/
    /** TODO 防止重复提交的令牌 幂等性**/
    @Getter @Setter
    private String orderToken;
    @Getter @Setter
    Map<Long,Boolean> stocks;

    public Integer getCount() {
        Integer count = 0;
        if (items != null && items.size() > 0) {
            for (OrderItemVo item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    /** 总商品金额 **/
    //BigDecimal total;
    //计算订单总额
    public BigDecimal getTotal() {
        BigDecimal totalNum = BigDecimal.ZERO;
        if (items != null && items.size() > 0) {
            for (OrderItemVo item : items) {
                //计算当前商品的总价格
                BigDecimal itemPrice = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                //再计算全部商品的总价格
                totalNum = totalNum.add(itemPrice);
            }
        }
        return totalNum;
    }

    /** 应付总额 **/
    //BigDecimal payPrice;
    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
