package com.atguigu.gulimall.cart.to;

import lombok.Data;
import lombok.ToString;

/**
 * 用户信息
 **/
@ToString
@Data
public class UserInfoTo {
    private Long userId;
    private String userKey; // 关联购物车
    private boolean tempUser = false;// 客户端是否需要存储cookie：user-key
}
