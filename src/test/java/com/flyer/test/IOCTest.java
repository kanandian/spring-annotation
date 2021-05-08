package com.flyer.test;

import com.flyer.bean.Person;
import com.flyer.config.MainConfigAddBean;
import com.flyer.config.MainConfigAddBean2;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

public class IOCTest {
    // @ComponentScan
    @Test
    public void test01() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigAddBean.class);
        String[] definitionNames = applicationContext.getBeanDefinitionNames(); // 获取容器中定义的所有的类

        for (String name: definitionNames) {
            System.out.println(name);
        }
    }

    // @Scope
    @Test
    public void test02() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigAddBean2.class);
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

    // @Conditional
    @Test
    public void test03() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigAddBean2.class);
        String[] personNames = applicationContext.getBeanNamesForType(Person.class); // 获取容器中定义的所有的类
        Map<String, Person> persons = applicationContext.getBeansOfType(Person.class);

//        Environment environment = applicationContext.getEnvironment();

        for (String name: personNames) {
            System.out.println(name);
        }
        System.out.println(persons);
    }


    // ImportSelector
    // ImportBeanDefinitionRegistrar
    @Test
    public void test04() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigAddBean2.class);
        printAllBeans(applicationContext);

        // FactoryBean获取的是getObject()方法返回的实例的类型
        Object bean1 = applicationContext.getBean("color");
        Object bean2 = applicationContext.getBean("color");
        System.out.println(bean1.getClass());
        System.out.println(bean1 == bean2);

        // 获取FactoryBean本身
        Object bean3 = applicationContext.getBean("&color");
        System.out.println(bean3.getClass());
    }

    private void printAllBeans(ApplicationContext applicationContext) {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String name: beanNames) {
            System.out.println(name);
        }
    }
}
