package com.atguigu.gulimall.thirdparty;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

//    @Autowired
//    OSSClient ossClient;
//    @Autowired
//    SmsComponent smsComponent;
//
//    @Test
//    void testSms() {
//        smsComponent.sendCode("16602789607", "996655");
//    }
//
//    @Test
//    void sendSMs(){
//        String host = "https://gyytz.market.alicloudapi.com";
//        String path = "/sms/smsSend";
//        String method = "POST";
//        String appcode = "213409bfb6f64fcd9478fda69f2251b1";
//        Map<String, String> headers = new HashMap<String, String>();
//        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
//        headers.put("Authorization", "APPCODE " + appcode);
//        Map<String, String> querys = new HashMap<String, String>();
//        querys.put("mobile", "16602789607");
//        querys.put("param", "**code**:123455");
//        querys.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
//        querys.put("templateId", "f7e31e0d8c264a9c8d6e9756de806767");
//        Map<String, String> bodys = new HashMap<String, String>();
//
//
//        try {
//            /**
//             * 重要提示如下:
//             * HttpUtils请从
//             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
//             * 下载
//             *
//             * 相应的依赖请参照
//             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
//             */
//            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
//            System.out.println(response.toString());
//            //获取response的body
//            //System.out.println(EntityUtils.toString(response.getEntity()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    void testUpload() throws FileNotFoundException {
////        // Endpoint以杭州为例，其它Region请按实际情况填写。
////        String endpoint = "oss-cn-shanghai.aliyuncs.com";
////        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
////        String accessKeyId = "XX";
////        String accessKeySecret = "XX";
////
////        // 创建OSSClient实例。
////        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//        // 上传文件流。
//        InputStream inputStream = new FileInputStream("C:\\Users\\Administrator\\Desktop\\product-mapping.txt");
//        ossClient.putObject("gulimall-wan", "hahaha.pdf", inputStream);
//
//        // 关闭OSSClient。
//        ossClient.shutdown();
//
//        System.out.println("上传成功");
//    }
//
//    @Test
//    void contextLoads() {
//    }

}
