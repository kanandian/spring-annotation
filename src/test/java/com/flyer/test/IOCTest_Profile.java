package com.flyer.test;

import com.flyer.bean.Car;
import com.flyer.bean.DataSource;
import com.flyer.bean.Holder;
import com.flyer.config.MainConfigOfAutowired;
import com.flyer.config.MainConfigOfProfile;
import com.flyer.service.BookService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IOCTest_Profile {

    @Test
    public void test01() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.getEnvironment().setActiveProfiles("test", "dev");
        applicationContext.register(MainConfigOfProfile.class);
        applicationContext.refresh();



        String[] beanNamesForType = applicationContext.getBeanNamesForType(DataSource.class);
        for (String name: beanNamesForType) {
            System.out.println(name);
        }
    }

    private void printAllBeans(ApplicationContext applicationContext) {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String name: beanNames) {
            System.out.println(name);
        }
    }
}
