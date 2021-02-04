package com.flyer.test;

import com.flyer.aop.MathCalculator;
import com.flyer.config.MainConfigOfAOP;
import com.flyer.ext.ExtConfig;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IOCTest_Ext {

    @Test
    public void test01() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ExtConfig.class);

        // 向Spring容器中发布事件
        applicationContext.publishEvent(new ApplicationEvent("我发布了事件"){});

        applicationContext.close();
    }

    private void printAllBeans(ApplicationContext applicationContext) {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String name: beanNames) {
            System.out.println(name);
        }
    }
}
