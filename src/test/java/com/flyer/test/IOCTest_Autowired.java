package com.flyer.test;

import com.flyer.bean.Car;
import com.flyer.bean.Holder;
import com.flyer.bean.Red;
import com.flyer.config.MainConfigOfAutowired;
import com.flyer.service.BookService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IOCTest_Autowired {
    private AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigOfAutowired.class);

    @Test
    public void test01() {
        BookService bookService = applicationContext.getBean(BookService.class);
        bookService.printBookDao();
        Holder holder = applicationContext.getBean(Holder.class);
        Car car = applicationContext.getBean(Car.class);
        System.out.println(holder.getCar() == car);
        System.out.println(holder);

    }

    private void printAllBeans(ApplicationContext applicationContext) {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String name: beanNames) {
            System.out.println(name);
        }
    }
}
