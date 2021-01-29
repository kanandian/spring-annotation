package com.flyer.config;

import com.flyer.bean.Car;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * bean的生命周期：
 *  bean的创建-->初始化-->销毁的过程
 *  容器管理bean的生命周期
 *  我们可以自定义初始化和销毁方法
 *
 * 单实例：
 *  初始化方法：对象创建完成，并调用构造器，然后调用初始化方法
 *  销毁方法：容器销毁时进行销毁
 * 多实例：
 *  初始化方法：容器创建完成后，获取组件时才会调用构造器和初始化方法
 *  销毁方法：容器创建了bean，但不管理bean，不会调用销毁方法，可以手动调用
 *
 *  自定义初始化和销毁方法的4中方式：
 *      1. 指定初始化和销毁方法
 *          (1) beans.xml中使用bean标签中init-method和destory-method属性指定方法
 *          (2) 注解的方式：在@Bean注解中指使用initMethod和destoryMethod属性指定
 *      2. 通过让容器实现InitializingBean接口来实现初始化逻辑，实现DisposableBean接口来实现销毁逻辑
 *      3. JSR250：使用@PostConstruct注解和@PreDestroy注解来实现，标注在bean类代码中对应的方法上
 *      4. BeanPostProcessor接口：bean的后置处理器
 *          在bean的初始化前后进行一些处理操作,实现方法：
 *              postProcessBeforeInitialization()：在任何初始化方法之前调用
 *              postProcessAfterInitialization()：在任何初始化方法之后调用
 *
 * 初始化过程底层函数调用过程：
 * populateBean(beanName, mbd, instanceWrapper);    // 给bean进行属性赋值
 * initializeBean() {
 *     applyBeanPostProcessorsBeforeInitialization(bean, beanName); 执行BeanPostProcessor的postProcessBeforeInitialization()方法
 *      获取到容器中所有的BeanPostProcessor，遍历执行postProcessBeforeInitialization()方法（一旦该方法返回null，跳出循环，后面的BeanPostProcessor便不再执行）
 *    invokeInitMethods(beanName, wrappedBean, mbd);   执行初始化方法
 *    applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
 *      执行逻辑与applyBeanPostProcessorsBeforeInitialization方法类似
 * }
 *
 * spring底层对BeanPostProcessor的使用（使用非常广泛）
 *  ApplicationContextAwareProcessor: bean继承ApplicationContextAware（向bean中注入applicationContext），由ApplicationContextAwareProcessor实现此功能，该类也是实现了BeanPostProcessor接口并在postProcessBeforeInitialization方法中实现了该逻辑
 *  BeanValidationPostProcessor: 参数校验
 *  InitDestroyAnnotationBeanPostProcessor: 实现了@PostConstruct和@PreDestroy的使用
 *  AutowiredAnnotationBeanPostProcessor: 实现自动注入
 * @Async注解（这里没有讲）
 */

@Configuration
@ComponentScan("com.flyer")
public class MainConfigOfLifeCycle {

    @Bean(initMethod = "init", destroyMethod = "destory")
    public Car car() {
        return new Car();
    }

}
