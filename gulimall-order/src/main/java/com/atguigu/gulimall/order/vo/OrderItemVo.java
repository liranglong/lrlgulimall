package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 购物项内容
 * @Created: with IntelliJ IDEA.
 * @author: wan
 */
@Data
public class OrderItemVo {
    private Long skuId;             // skuId
    private Boolean check = true;   // 是否选中
    private String title;           // 标题
    private String image;           // 图片
    private List<String> skuAttrValues;// 商品销售属性
    private BigDecimal price;       // 单价
    private Integer count;          // 当前商品数量
    private BigDecimal totalPrice;  // 总价
    private BigDecimal weight = new BigDecimal("0.085");// 商品重量
}
