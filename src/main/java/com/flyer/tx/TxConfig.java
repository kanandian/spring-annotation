package com.flyer.tx;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 *
 * 声明式事务：
 *  环境搭建：
 *      1. 导入相关依赖：数据源、数据库驱动、spring-jdbc模块
 *      2. 配置数据源、JdbcTemplate(Spring提供简化数据库操作的工具)操作数据库
 *      3、使用@EnableTransactionManagement开启事务管理功能
 *      4. 配置事务管理器
 *      5. 在服务类方法上表上@Transactional表示当前方法是一个事务方法
 *
 *
 * 原理：
 *  1. @EnableTransactionManagement
 *      利用TransactionManagementConfigurationSelector向容器中导入两个组件
 *          AutoProxyRegistrar
 *          ProxyTransactionManagementConfiguration
 *  2. AutoProxyRegistrar：向容器中注册InfrastructureAdvisorAutoProxyCreator组件（类似之前@EnableAspectJAutoProxy）
 *      InfrastructureAdvisorAutoProxyCreator：
 *          利用后置处理器机制在对象创建以后，包装对象，返回一个代理对象(增强机制)，代理对象执行方法利用拦截器链进行调用（逻辑和aop那部分一样）
 *  3. ProxyTransactionManagementConfiguration：
 *      (1) 向容器中注册事务增强器
 *          1. 事务增强器要使用事务注解信息（事务的属性）：使用AnnotationTransactionAttributeSource解析事务注解
 *          2. 事务拦截器：TransactionInterceptor：保存了事务属性信息以及事务管理器
 *              TransactionInterceptor是一个MethodInteceptor: 目标方法执行时执行拦截器链
 *                  1. 先获取事务相关的属性
 *                  2. 获取PlatformTransactionManager，若事先没有添加指定的TransactionManager，最终会从容器中按照类型获取一个PlatformTransactionManager
 *                  3. 执行目标方法
 *                      若发生异常，获取到事务管理器，利用其回滚这次操作（回滚操作是由事务管理器来做的，TransactionInterceptor这是来拦截）
 *                      若正常，利用事务管理器提交事务
 */

@EnableTransactionManagement
@Configuration
@ComponentScan("com.flyer.tx")
public class TxConfig {
    @Bean
    public DataSource dataSource() throws PropertyVetoException {
        // 创建数据源，这里使用c3p0数据库连接池
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setUser("root");
        dataSource.setPassword("Qzf19960914");
        dataSource.setDriverClass("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/springtest?useUnicode=true&amp&characterEncoding=utf-8&serverTimezone=GMT%2b8");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() throws PropertyVetoException {
        // 注意这里不会创建两个DataSource（Spring对配置文件的特殊处理，向容器中加组件的方法，多次调用都只是从容器中找组件，而不会重新创建）
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
        return jdbcTemplate;
    }

    // 在容器中注册事务管理器
    @Bean
    public PlatformTransactionManager transactionManager() throws PropertyVetoException {
        return new DataSourceTransactionManager(dataSource());
    }
}
