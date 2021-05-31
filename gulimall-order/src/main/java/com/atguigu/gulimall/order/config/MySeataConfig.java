package com.atguigu.gulimall.order.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

//import io.seata.rm.datasource.DataSourceProxy;

/**
 * seata分布式事务
 * 配置代理数据源
 */
//@Configuration
public class MySeataConfig {
    @Autowired
    DataSourceProperties dataSourceProperties;

    /**
     * 自动配置类，如果容器中存在数据源就不自动配置数据源了
     */
//    @Bean
//    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
//        HikariDataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
//        if (StringUtils.hasText(dataSourceProperties.getName())) {
//            dataSource.setPoolName(dataSourceProperties.getName());
//        }
//        return new DataSourceProxy(dataSource);
//    }
}
