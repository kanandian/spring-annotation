package com.flyer.test;

import com.flyer.aop.MathCalculator;
import com.flyer.config.MainConfigOfAOP;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IOCTest_AOP {

    @Test
    public void test01() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigOfAOP.class);

        // 不要自己创建
        MathCalculator mathCalculator = new MathCalculator();
        mathCalculator.div(1, 1);   // 不会触发aop，因为不在容器中

        mathCalculator = applicationContext.getBean(MathCalculator.class);
        mathCalculator.div(1, 1);   // 触发aop
        mathCalculator.div(1, 0);   // 触发aop


//        String[] beanNamesForType = applicationContext.getBeanNamesForType(DataSource.class);
//        for (String name: beanNamesForType) {
//            System.out.println(name);
//        }
    }

    private void printAllBeans(ApplicationContext applicationContext) {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String name: beanNames) {
            System.out.println(name);
        }
    }
}
