package com.atguigu.gulimall.search;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallSearchApplicationTests {

//    @Autowired
//    private RestHighLevelClient client;
//
//    @Data
//    static class Account {
//        private int account_number;
//        private int balance;
//        private String firstname;
//        private String lastname;
//        private int age;
//        private String gender;
//        private String address;
//        private String employer;
//        private String email;
//        private String city;
//        private String state;
//
//        @Override
//        public String toString() {
//            return "Account{" +
//                    "account_number=" + account_number +
//                    ", balance=" + balance +
//                    ", firstname='" + firstname + '\'' +
//                    ", lastname='" + lastname + '\'' +
//                    ", age=" + age +
//                    ", gender='" + gender + '\'' +
//                    ", address='" + address + '\'' +
//                    ", employer='" + employer + '\'' +
//                    ", email='" + email + '\'' +
//                    ", city='" + city + '\'' +
//                    ", state='" + state + '\'' +
//                    '}';
//        }
//    }
//
//    /**
//     * 从es中查询数据
//     * 1、创建查询请求
//     *      SearchRequest searchRequest = new SearchRequest("newbank")
//     * 2、创建构建DSL检索条件对象
//     *      SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
//     * 3、创建各种条件
//     *      QueryBuilder boolQuery = QueryBuilders.boolQuery()
//     *      QueryBuilder matchQuery = QueryBuilders.matchAllQuery()
//     *
//     * 4、组合检索条件
//     *         sourceBuilder.sort();
//     *         sourceBuilder.from();
//     *         sourceBuilder.size();
//     *         sourceBuilder.aggregation();
//     *         sourceBuilder.query(QueryBuilder);
//     *         sourceBuilder.query(boolQuery);
//     * 5、请求绑定条件
//     *      searchRequest.source(sourceBuilder);
//     */
//    @Test
//    void searchData() throws IOException {
//        // 1、创建检索请求，自定索引【也可以调用这个方法指定searchRequest.indices("bank")】
//        SearchRequest searchRequest = new SearchRequest("newbank");
//        // 2、指定检索条件 DSL
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        // 3、构建检索条件
//        // sourceBuilder.sort();
//        // sourceBuilder.from();
//        // sourceBuilder.size();
//        // sourceBuilder.aggregation();
//        // sourceBuilder.query(QueryBuilders.matchAllQuery());
//        // 需求：1、检索address中包含mill
//        //       2、年龄分布
//        //       3、平均薪资
//        sourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
//        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("ageAgg").field("age").size(10);
//        sourceBuilder.aggregation(termsAggregationBuilder);
//
//        AvgAggregationBuilder avgAggregationBuilder = AggregationBuilders.avg("balanceAvg").field("balance");
//        sourceBuilder.aggregation(avgAggregationBuilder);
//        System.out.println("检索条件:" + sourceBuilder);
//        // 4、绑定检索条件与请求
//        searchRequest.source(sourceBuilder);
//        // 5、执行请求，获取结果
//        SearchResponse searchResponse = client.search(searchRequest, GulimallElasticSearch.COMMON_OPTIONS);
//
//        // 6、获取所有查到的数据【可以转成map =》JSON.parseObject(searchResponse.toString(), Map.class)】
//        SearchHit[] hits = searchResponse.getHits().getHits();
//        for (SearchHit hit : hits) {
//            // "hits" : [
//            //      {
//            //        "_index" : "newbank",
//            //        "_type" : "_doc",
//            //        "_id" : "10",
//            //        "_score" : null,
//            //        "_source" : {
//            //          "account_number" : 10,
//            //          "balance" : 46170,
//            //          "firstname" : "Dominique",
//            //          "lastname" : "Park",
//            //          "age" : 37,
//            //          "gender" : "F",
//            //          "address" : "100 Gatling Place",
//            //          "employer" : "Conjurica",
//            //          "email" : "dominiquepark@conjurica.com",
//            //          "city" : "Omar",
//            //          "state" : "NJ"
//            //        },
//            //        "sort" : [
//            //          10
//            //        ]
//            //      }
////            hit.getIndex();hit.getType();hit.getId();
//            String sourceJsonString = hit.getSourceAsString();
//            Account account = JSON.parseObject(sourceJsonString, Account.class);
//            System.out.println("搜索结果： " + account);
//        }
//
//        Aggregations aggregations = searchResponse.getAggregations();
//        Terms ageAgg = aggregations.get("ageAgg");
//        for (Terms.Bucket bucket : ageAgg.getBuckets()) {
//            System.out.println("年龄：" + bucket.getKeyAsString() + "--人数： " + bucket.getDocCount());
//        }
//
//        Avg balanceAvg = aggregations.get("balanceAvg");
//        System.out.println("薪资平均值： " + balanceAvg.getValue());
//
//    }
//
//    @Test
//    void indexData() throws IOException {
//        // 1、构建创建或更新请求，指定索引users
//        IndexRequest indexRequest = new IndexRequest("users");
//        // 2、设置id
//        indexRequest.id("1");// 数据的id
//        // 方式一：设置保存的内容
//        //users.source("userName", "zhangsan", "gender", "M", "age", "18");
//        User user = new User("lisi", "M", 22);
//        // 3、绑定数据与请求 方式二：设置保存的内容【json格式】
//        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);
//
//        // 4、执行：同步
//        IndexResponse index = client.index(indexRequest, GulimallElasticSearch.COMMON_OPTIONS);
//        // 5、提取响应数据
//        System.out.println(index);
//        // 异步：
//    }
//
//    @Data
//    class User{
//        private String userName;
//        private String gender;
//        private Integer age;
//
//        public User(String userName, String gender, Integer age) {
//            this.userName = userName;
//            this.gender = gender;
//            this.age = age;
//        }
//    }

}
