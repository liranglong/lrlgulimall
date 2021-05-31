package com.atguigu.gulimall.ware.config;

//import io.seata.rm.datasource.DataSourceProxy;

/**
 *  seata分布式事务
 *  配置代理数据源
 * @author: wanzenghui
 **/
//@Configuration
public class MySeataConfig {

//    @Autowired
//    DataSourceProperties dataSourceProperties;
//
//    /**
//     * 需要将 DataSourceProxy 设置为主数据源，否则事务无法回滚
//     */
//    @Bean
//    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
//        HikariDataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
//        if (StringUtils.hasText(dataSourceProperties.getName())) {
//            dataSource.setPoolName(dataSourceProperties.getName());
//        }
//        return new DataSourceProxy(dataSource);
//    }
}
