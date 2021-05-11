package com.flyer.config;

import com.flyer.bean.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringValueResolver;

/**
 *
 * Profile: Spring提供的可以根据当前环境，动态的激活和切换一系列组件的功能（特别好用）
 * 开发环境、测试环境、生产环境
 * 例如：数据源在不同环境中连接不同的数据库
 * @Profile("environment"): 加了环境标识后，只有这个环境被激活才能注册到容器中，默认是default
 * 没有标注环境标识的bean在任何情况下都会加载
 *
 * 标注位置：
 *  1. @Bean标注的方法
 *  2. 配置类：只有在指定环境下，配置类中所有配置才能生效
 *
 * 如何切换环境：
 *  1. 使用命令行动态参数：-Dspring.profiles.active=test
 *  2. 在代码中激活（test.IOCTest_Profile中有示例）
 *      (1) 创建applicationContext(注意这里不要添加配置类)：AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
 *      (2) 设置需要激活的环境：applicationContext.getEnvironment().setActiveProfiles("test", "dev");
 *      (3) 注册主配置类：applicationContext.register(MainConfigOfProfile.class);
 *      (4) 刷新容器：applicationContext.refresh();
 *
 */

@PropertySource(value = {"classpath:/dbconfig.properties"})
@Configuration
public class MainConfigOfProfile implements EmbeddedValueResolverAware {
    @Value("db.user")
    private String user;
    private StringValueResolver stringValueResolver;
    private String driverClass;

    @Profile("test")
    @Bean("testDataSource")
    public DataSource dataSourceTest(@Value("${db.password}") String password) {
        DataSource dataSource = new DataSource();
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        dataSource.setDriverClass(driverClass);

        return dataSource;
    }

    @Profile("dev")
    @Bean("devDataSource")
    public DataSource dataSourceDev(@Value("${db.password}") String password) {
        DataSource dataSource = new DataSource();
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        dataSource.setDriverClass(driverClass);

        return dataSource;
    }

    @Profile("pro")
    @Bean("proDataSource")
    public DataSource dataSourcePro(@Value("${db.password}") String password) {
        DataSource dataSource = new DataSource();
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        dataSource.setDriverClass(driverClass);

        return dataSource;
    }

    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        this.stringValueResolver = stringValueResolver;
        this.driverClass = stringValueResolver.resolveStringValue("$(db.driverClass)");
    }
}
