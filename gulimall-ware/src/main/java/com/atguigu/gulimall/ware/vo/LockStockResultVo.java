package com.atguigu.gulimall.ware.vo;

import lombok.Data;

/**
 * 库存锁定结果Vo，每一个Item一个结果
 * @author: wanzenghui
 **/

@Data
public class LockStockResultVo {
    private Long skuId;
    private Integer num;
    /** 是否锁定成功 **/
    private Boolean locked;
}
