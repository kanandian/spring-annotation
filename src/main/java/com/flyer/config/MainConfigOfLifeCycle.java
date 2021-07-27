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
 *          * @PostConstruct: bean创建完成并完成属性赋值后执行
 *          * @PreDestroy：bean将要被移除之前被调用
 *      4. BeanPostProcessor接口（很重要Spring底层应用十分广泛）：bean的后置处理器（所有组件初始化前后都会调用到BeanPostProcessor，以上3种方法只能管理单个类别的组件）
 *          在bean的初始化前后进行一些处理操作,实现方法：
 *              （构造方法）
 *              postProcessBeforeInitialization()：在任何初始化方法之前调用
 *              （init_method）
 *              postProcessAfterInitialization()：在任何初始化方法之后调用
 *          返回值可以是原来的对象，也可以包装一下再返回（代理对象）
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
 *  AsyncAnnotationBeanPostProcessor: @Async注解
 *  ApplicationContextAwareProcessor: bean继承ApplicationContextAware（向bean中注入applicationContext），由ApplicationContextAwareProcessor实现此功能，该类也是实现了BeanPostProcessor接口并在postProcessBeforeInitialization方法中实现了该逻辑
 *  BeanValidationPostProcessor: 参数校验
 *  InitDestroyAnnotationBeanPostProcessor: 实现了@PostConstruct和@PreDestroy的使用（反射）
 *  AutowiredAnnotationBeanPostProcessor: 实现自动注入
 *      (特别注意：@Autowired的注入过程不是在initializeBean()中常规后置处理器的过程注入的，而是在populateBean方法中调用)
 *      populateBean()方法：对@Autowired、@Resource、@Value注解标注的属性进行填充,
 *      以及对beanDefinition.getPropertyValues获取到的属性进行处理
 *      总体流程：
 *          <1> 如果注入模型为byName/byType, 则为自动注入, 自动注入的时候, Spring会扫描所有的get/set方法,
 * 如果一个set方法有参数, 则Spring就会将这些方法去除set前缀, 以set后面的字符串认为是一个属性名, 将其
 * 封装成一个PropertyValue, 就像是程序员通过beanDefinition.getPropertyValues来提供参数一样, 到了之
 * 后在处理PropertyValues的时候, 就会取出一个个的PropertyValue, 调用里面属性对应的set方法完成自动注
 * 入, 所以由这个可以看出, Spring在处理程序员提供的PropertyValue的时候, 必须要有set方法才能完成的,
 * 由此, 我们发现其实自动注入最终还是利用了PropertyValue的处理逻辑而已, 这里需要注意的是, 在获取这些
 * set方法的时候, Spring不是自己手动的扫描所有的method, 而是直接利用了Java内置的内省机制来完成的
 * <2> 开始处理@Autowired等注解标注的属性/方法, 其实就是拿到之前applyMergedBeanDefinitionPostProcessord
 * 方法调用时产生的injectedMetadata, 遍历一个个的injectedElement(包含了属性/方法相关的信息), 从容器
 * 中查找bean, 之后通过反射调用方法或者属性
 * <3> Spring开始处理PropertyValues, 其实很简单, 就是通过set方法调用来处理我们手动放入的属性的
 *
 * 作者：zhongshenglong
 * 链接：https://juejin.cn/post/6844904167945797646
 * 来源：掘金
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 *
 *
 *
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
