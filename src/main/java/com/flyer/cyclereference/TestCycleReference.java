package com.flyer.cyclereference;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestCycleReference {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class);
        Object dbb = applicationContext.getBean("dbb");
        Object xbb = applicationContext.getBean("xbb");
        System.out.println(dbb);
        System.out.println(xbb);
    }
}
