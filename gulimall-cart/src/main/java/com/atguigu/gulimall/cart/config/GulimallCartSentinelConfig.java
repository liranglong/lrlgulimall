package com.atguigu.gulimall.cart.config;

import org.springframework.context.annotation.Configuration;

/**
 * 自定义阻塞返回方法
 **/

@Configuration
public class GulimallCartSentinelConfig {
//    public GulimallCartSentinelConfig() {
//        WebCallbackManager.setUrlBlockHandler(new UrlBlockHandler() {
//            @Override
//            public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws IOException {
//                R error = R.error(BizCodeEnume.TO_MANY_REQUEST.getCode(), BizCodeEnume.TO_MANY_REQUEST.getMsg());
//                response.setCharacterEncoding("UTF-8");
//                response.setContentType("application/json");
//                response.getWriter().write(JSON.toJSONString(error));
//            }
//        });
//    }
}
