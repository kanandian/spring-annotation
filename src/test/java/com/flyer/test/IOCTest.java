package com.flyer.test;

import com.flyer.config.MainConfig;
import com.flyer.config.MainConfig2;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IOCTest {
    @Test
    public void test01() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class);
        String[] definitionNames = applicationContext.getBeanDefinitionNames(); // 获取容器中定义的所有的类

        for (String name: definitionNames) {
            System.out.println(name);
        }
    }

    @Test
    public void test02() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig2.class);
        String[] definitionNames = applicationContext.getBeanDefinitionNames(); // 获取容器中定义的所有的类

        for (String name: definitionNames) {
            System.out.println(name);
        }

        // 默认是单实例的，当配置@Scope("prototype")时，是多实例的（该注解相当于bean标签中的scope属性）
        // 单实例情况下ioc容器启动时会调用@Bean的方法创建对象，放入容器
        // 多实例情况下只有要获取实例时才会调用@Bean方法创建对象，且获取几次就调用几次
        Object person = applicationContext.getBean("person");
        Object person2 = applicationContext.getBean("person");
        System.out.println(person == person2);
    }
}
