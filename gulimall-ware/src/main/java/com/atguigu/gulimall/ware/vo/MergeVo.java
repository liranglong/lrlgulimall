package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: wanzenghui
 **/

@Data
public class MergeVo {

    private Long purchaseId;

    private List<Long> items;

}
