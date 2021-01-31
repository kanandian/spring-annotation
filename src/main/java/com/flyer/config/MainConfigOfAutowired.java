package com.flyer.config;

import com.flyer.bean.Car;
import com.flyer.bean.Holder;
import com.flyer.dao.BookDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.awt.print.Book;

/**
 * 自动装配：
 *  Spring利用依赖注入（DI），完成对IOC容器中各个组件的以来关系赋值
 *      1. @Autowired: 自动注入
 *          (1) 默认优先按照类型寻找，找到就赋值
 *          (2) 如果找到多个相同类型的组件，再将属性名作为组件的id去寻找
 *          (3) @Qualifier("bookDao") 明确指定使用哪个组件id，而不是使用属性名
 *          (4) 自动装配若找不到对应组件默认就会报错, 使用@Autowired(required = false)将自动装配设置为非必须的
 *          (5) @Primary: 让spring自动装配的时候默认使用首选的bean，（也可以使用@Qualifier具体指定哪个bean）
 *      2. @Resource(JSR250中定义)
 *          与@Autowired类似，默认按照组件名称进行装配(可以使用@Resource(name="name"))指定名称，但不支持@Primary，不能结合@Qualifier，也没有required属性
 *      3. @Inject(JSR330中定义)
 *          需要添加javax.inject的包，和@Autowired功能类似，支持@primary和@Qualifier，但是不能使用required属性
 *
 * 注意：@Autowired是spring定义的，而@Resource和@Inject都是java规范
 * AutowiredAnnotationBeanPostProcessor来实现解析并自动装配的功能
 *
 *      4. @Autowired：构造器、参数、属性、方法都能标注(详情见Holder类)
 *      5. 自定义组件想要使用Spring容器底层的一些组件（例如：ApplicationContext、BeanFactory、XXX、...）(Red类中讲解)
 *          让自定义实现XXXAware(参考生命周期中ApplicationContextAwareProcessor的用法),在创建对象的时候会调用接口规定的方法，注入相关组件
 *          XXXAware的功能由XXXAwareProcessor实现
 *
 *
 *
 */

@Configuration
@ComponentScan({"com.flyer.controller", "com.flyer.service", "com.flyer.dao", "com.flyer.bean"})
public class MainConfigOfAutowired {
    @Primary
    @Bean("bookDao2")
    public BookDao bookDao() {
        return new BookDao();
    }


    /**
     * @Bean标注时，方法参数会自动从ioc容器中获取，默认不写@Autowired
     * @param car
     * @return
     */
    @Bean
    @Autowired  // 默认不写
    public Holder holder(Car car) {
        return new Holder(car);
    }
}
