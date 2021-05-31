package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: wanzenghui
 **/

@Data
public class FareVo {

    private MemberAddressVo address;

    private BigDecimal fare;

}


