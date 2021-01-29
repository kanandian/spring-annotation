package com.flyer.test;

import com.flyer.bean.Person;
import com.flyer.config.MainConfigOfPropertyValues;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

public class IOCTest_PropertyValues {
    private AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigOfPropertyValues.class);

    @Test
    public void test01() {
        // 主要用到@Value注解（在Person类中详解）
        printAllBeans(applicationContext);
        Person person = (Person) applicationContext.getBean("person");
        System.out.println(person);

        // 使用@PropertiesSource读取外部配置文件中属性（key、value）保存到运行的环境变量中（Environment）,保存后可以用${key}来取出配置文件中的值
        // 也可以直接在Environment中直接获取属性值
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String nickName = environment.getProperty("person.nickName");
        System.out.println(nickName);

        applicationContext.close();

    }

    private void printAllBeans(ApplicationContext applicationContext) {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String name: beanNames) {
            System.out.println(name);
        }
    }
}
