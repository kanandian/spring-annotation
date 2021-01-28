package com.flyer.config;

import com.flyer.bean.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class MainConfig2 {

    /**
     * @Scope()注解的值：
     * prototype: 多实例的：只有要获取实例时才会调用@Bean方法创建对象，且获取几次就调用几次
     * singleton: 单实例的(默认值)：ioc容器启动时会调用@Bean的方法创建对象，放入容器
     * 以下是web环境下独有的，却一般用不到:
     * request: 同一个请求（request）创建一个实例
     * session: 同一个session创建一个实例
     * @return
     */

    @Bean
    @Scope("prototype")
    public Person person() {
        return new Person("wangwu", 27);
    }
}
