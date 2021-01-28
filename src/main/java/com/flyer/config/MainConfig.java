package com.flyer.config;


import com.flyer.bean.Person;
import com.flyer.service.BookService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

// 配置类等同于配置文件（beans.xml）
@Configuration  // 告诉Spring这是一个配置类
@ComponentScan(value = "com.flyer", excludeFilters = {
//        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Controller.class}),   // 按照注解过滤，过滤标了@Controller注解的类
//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {BookService.class}), // 按照指定类型过滤
        @ComponentScan.Filter(type = FilterType.CUSTOM, classes = {MyTypeFilter.class}) // 自定义过滤规则
}) // 包扫描：只要类中标注了@Controller、@Service、@Repository、@Component，该类实例都会被自动扫描，并加进容器
// jdk8以后可以重复写@ComponentScan注解，若是jdk8之前，则可用@ComponentScans = ComponentScan[] 指定多个@ComponentScan注解
// excludeFilters = Filter[]: 指定扫描时按照某种过滤规则排除某些组件
// includeFilters = Filter[]: 指定扫描时只包含哪些组件（使用方法和excludeFilters一样，同时设置useDefaultFilters = false）
// 过滤规则：
// FilterType.ANNOTATION: 使用注解的方式
// FilterType.ASSIGNABLE_TYPE: 按照给定的类型
// FilterType.ASPECTJ: 使用ASPECTJ表达式
// FilterType.REGEX: 使用正则表达式
// FilterType.CUSTOM: 使用自定义规则（自定义规则必须为TypeFilter的实现类）
public class MainConfig {
    @Bean("aPerson")   // 向容器中注册一个bean（相当于beans.xml中的bean标签）类型为返回值类型，id默认为方法名
    // @Bean注解中value值可以指定bean的id
    public Person person() {
        return new Person("lisi", 25);
    }
}
