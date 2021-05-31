package com.atguigu.gulimall.ssoclient.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试单点登录
 */
@Controller
public class HelloController {


    /**
     * 无需登录就可访问
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/hello")
    public String hello() {
        return "hello";
    }


    @GetMapping(value = "/boss")
    public String employees(Model model, HttpSession session,
                            @RequestParam(value = "token", required = false) String token,
                            @RequestParam(name = "token", required = false) String token2) {
        List<String> emps = new ArrayList<>();
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null && token == null) {
            //没登录,跳转到服务器登录
            return "redirect:http://ssoserver.com:8080/login.html?redirect_url=http://client2.com:8082/boss";
        } else {
            emps.add("张三");
            emps.add("李四");
            model.addAttribute("emps",emps);
            System.out.println(token);
            System.out.println(token2);
            return "employees";
        }
    }
}

