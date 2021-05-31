package com.atguigu.gulimall.ssoserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class LoginController {

    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping(value = "/login.html")
    public String login(@RequestParam("redirect_url") String url,
                        Model model,
                        @CookieValue(value = "sso_token", required = false) String sso_token) {
        if (sso_token != null) {
            return "redirect:" + url + "?token=" + sso_token;
        }
        model.addAttribute("url", url);
        return "login";
    }

    @PostMapping(value = "/doLogin")
    public String doLogin(String username,
                          String password,
                          String url,
                          HttpServletResponse response) {

        String uuid = UUID.randomUUID().toString().replace("-", "");
        //登录成功跳转，跳回到之前的页面
        redisTemplate.opsForValue().set(uuid, username);
        Cookie cookie = new Cookie("sso_token", uuid);
        response.addCookie(cookie);
        return "redirect:" + url + "?token=" + uuid;
    }

}
