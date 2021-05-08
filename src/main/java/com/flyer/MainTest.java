package com.flyer;

import com.flyer.bean.Person;
import com.flyer.config.MainConfigAddBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainTest {
    public static void main(String[] args) {
        // 配置文件开发（过时了，现在都用注解）
//        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
//        Person person = (Person) applicationContext.getBean("person");
//        System.out.println(person);   // Person{name='zhangsan', age=18}

        // 注解式开发
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigAddBean.class);
//        Person person = (Person) applicationContext.getBean("person");
        Person person = (Person) applicationContext.getBean(Person.class);
        System.out.println(person); // Person{name='lisi', age=25}

        String[] personIDs = applicationContext.getBeanNamesForType(Person.class);
        for (String personId: personIDs) {
            System.out.println(personId);
        }
    }
}
