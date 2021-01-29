package com.flyer.test;

import com.flyer.config.MainConfigOfLifeCycle;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IOCTest_LifeCycle {
    @Test
    public void test01() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigOfLifeCycle.class);
        System.out.println("容器创建完成");
        // 先调用组件的构造器，然后调用init方法

        // 关闭容器，调用组件的destory方法(不关闭容器则不销毁组件)
        applicationContext.close();

    }
}
