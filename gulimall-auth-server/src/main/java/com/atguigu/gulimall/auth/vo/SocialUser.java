package com.atguigu.gulimall.auth.vo;

import lombok.Data;

/**
 * 发送code换取到的结果
 */
@Data
public class SocialUser {
    private String access_token;
    private String remind_in;
    private long expires_in;
    private String uid;
    private String isRealName;
}
