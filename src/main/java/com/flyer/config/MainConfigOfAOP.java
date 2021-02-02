package com.flyer.config;

import com.flyer.aop.LogAspects;
import com.flyer.aop.MathCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 *
 * AOP(面向切换编程)(原理：动态代理):
 *  在程序运行期间，动态的将某段代码切入到指定位置运行的编程方式
 *
 * 细节步骤：
 * 1. 导入AOP模块：Spring AOP: spring-aspects
 * 2. 创建一个业务逻辑类(例如：MathCalculator): 在业务逻辑运行时将日志进行打印（方法运行之前、方法结束、方法出现异常）
 * 3. 定义一个日志切面类(LogAspects): 切面类中的方法需要动态感知MathCalculator.div()方法的运行状态，然后执行
 *  通知方法：
 *      前置通知(@Before):
 *      后置通知(@After): 无论方法正常结束还是异常结束，都会执行
 *      返回通知(@AfterReturning):
 *      异常通知(@AfterThrowing):
 *      环绕通知(@Around)：动态代理，手动推进目标方法运行(joinPoint.procced())
 * 4. 给切面类的目标方法标注何时何地运行（通知注解）
 * 5. 将切面类和目标逻辑类都加入到容器中
 * 6. 告诉Spring哪个类是切面类（给切面类上加一个注解@Aspect）
 * 7. 给配置类中添加@EnableAspectJAutoProxy注解，开启基于注解的aop模式，类似于配置文件中的<aop:aspectj-autoproxy></aop:aspectj-autoproxy>
 *  在Spring中有很多@EnableXXX注解 开启某一项功能，代替以前的功能
 *
 *
 *  总结3步：
 *  1. 将业务逻辑组件和切面类都放到容器中，并告诉Spring哪个是切面类（@Aspect）
 *  2. 在切面类上的每一个通知方法上标注通知注解，告诉spring何时何地运行（切入点表达式）
 *  3. 开启基于注解的AOP表达式@EnableAspectJAutoProxy
 *
 *
 * AOP原理：(Spring原理看什么：看给容器中添加了什么组件，这个组件什么时候工作，有什么功能)
 *  1. @EnableAspectJAutoProxy
 *      使用@Import({AspectJAutoProxyRegistrar.class}): 向容器中加入AspectJAutoProxyRegistrar
 *          利用AspectJAutoProxyRegistrar自定义向容器中自定义注册bean
 *              registry.registerBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator", beanDefinition);
 *                  向容器中注册一个id为org.springframework.aop.config.internalAutoProxyCreator的beanDefinition
 *                  其中beanDefinition的类型是org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator
 *
 * 2. AnnotationAwareAspectJAutoProxyCreator:
 *  继承关系：
 *  AnnotationAwareAspectJAutoProxyCreator
 *      -> AspectJAwareAdvisorAutoProxyCreator
 *          -> AbstractAdvisorAutoProxyCreator
 *              -> AbstractAutoProxyCreator(implements SmartInstantiationAwareBeanPostProcessor, BeanFactoryAware)
 *                  这里关注bean的后置处理器（bean的初始化前后做的事情）、并通过BeanFactoryAware注入BeanFactory（注意不是FactoryBean）
 *               -> ProxyProcessorSupport
 *                  -> ProxyProcessorSupport
 *                      -> ProxyConfig
 *
 *  源码分析（打断点的位置）：
 *      AbstractAutoProxyCreator.setBeanFactory()
 *      AbstractAutoProxyCreator后置处理器方法
 *
 *      AbstractAdvisorAutoProxyCreator.setBeanFactory() -> initBeanFactory()
 *
 *      AspectJAwareAdvisorAutoProxyCreator.initBeanFactory()
 *
 *  具体流程（源码追踪）：
 *  =================创建以及注册AnnotationAwareAspectJAutoProxyCreator的过程=================================
 *      1. 启动类中创建IOC容器（IOCTest_AOP）
 *      2. 注册配置类，然后调用refresh方法刷新容器（创建IOC容器的过程）
 *          register(annotatedClasses): 注册类的配置信息（BeanDefinition）
 *          refresh(): 创建bean并做准备工作
 *      3. registerBeanPostProcessors(beanFactory);（refresh()方法中，注册bean的后置处理器，来拦截bean的创建）
 *          (1) 先获取ioc容器中已经定义还未创建的BeanPostProcessor的id
 *          (2) 给接口中添加别的BeanPostProcessor
 *          (3) 对BeanPostProcessor进行分类，然后根据分类依次注册:
 *              优先注册实现了PriorityOrdered接口的BeanPostProcessor
 *              然后注册实现了Ordered接口的BeanPostProcessor
 *              最后注册没实现优先级接口的BeanPostProcessor
 *          (4) 注册BeanPostProcessor(创建对象，并保存在容器中)（registerBeanPostProcessors(beanFactory, (List)orderedPostProcessors)：在beanFactory中注册生成的BeanPostProcessor）
 *              如何创建id为org.springframework.aop.config.internalAutoProxyCreator的BeanPostProcessor(AnnotationAwareAspectJAutoProxyCreator)
 *                  （AbstractAutowireCapableBeanFactory.doCreateBean()方法中）
 *                  1. createBeanInstance(beanName, mbd, args)：创建bean的实例
 *                  2. populateBean(beanName, mbd, instanceWrapper)：给bean的各个属性赋值
 *                  3. initializeBean(beanName, exposedObject, mbd)：初始化bean（调用初始化方法）
 *                      (1) invokeAwareMethods(beanName, bean): 处理Aware接口的方法回调
 *                          *在这里最终调用AbstractAdvisorAutoProxyCreator.setBeanFactory()方法
 *                      (以下3个方法在讲生命周期是讲过)
 *                      (2) applyBeanPostProcessorsBeforeInitialization(bean, beanName): 执行所有BeanPostProcessor的postProcessBeforeInitialization()方法
 *                      (3) invokeInitMethods(beanName, wrappedBean, mbd): 执行bean的初始化方法
 *                      (4) applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName): 执行所有BeanPostProcessor的postProcessAfterInitialization()方法
 *                  4. BeanPostProcessor(AnnotationAwareAspectJAutoProxyCreator)创建成功：得到aspectJAdvisorsBuilder(AnnotationAwareAspectJAutoProxyCreator.initBeanFactory()方法中)
 *          (5) 将BeanPostProcessor注册到BeanFactory中：(以上第(4)步中提到)
 *                  registerBeanPostProcessors(beanFactory, (List)orderedPostProcessors) -> beanFactory.addBeanPostProcessor(postProcessor);
 *
 * ================================以下是AnnotationAwareAspectJAutoProxyCreator作为后置处理器的作用=========================================
 *              AnnotationAwareAspectJAutoProxyCreator继承InstantiationAwareBeanPostProcessor(Instantiation前后（创建对象之前让后置处理器尝试返回代理对象时）)
 *              而InstantiationAwareBeanPostProcessor又继承BeanPostProcessor(Initialization前后（真正创建对象时）)
 *      4. finishBeanFactoryInitialization(beanFactory)：完成BeanFactory的初始化工作做，创建剩下的bean
 *          (1) 遍历容器中所有的Bean，依次创建对象getBean(beanName)
 *              getBean() -> doGetBean() -> getSingleton() -> createBean()
 *          (2) createBean(): 创建Bean   InstantiationAwareBeanPostProcessor会在任何bean创建之前尝试返回bean的代理实例
 *              先从缓存中获取当前Bean是否被创建，如果可以获取到，说明bean之前被创建过，直接包装使用，否则再创建
 *              只要创建好的bean都会被缓存起来（做到单实例: scope=singleton）
 *              1. resolveBeforeInstantiation(beanName, mbdToUse);
 *                  给后置处理器一个返回代理对象的机会，如果能返回代理对象就使用该代理对象，否则调用doCreateBean(beanName, mbdToUse, args)(该方法之前见过（先创建实例，后来populate、使用后置处理器，调用初始化方法）)真正创建bean实例和3.6流程一样
 *                  (1) 后置处理器先尝试返回代理对象
 *                      bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
 *                          拿到所有后置处理器，遍历，若是InstantiationAwareBeanPostProcessor的子类，则执行其applyBeanPostProcessorsBeforeInstantiation(targetType, beanName)方法
 * 					    if (bean != null) {
 * 						    bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
 *                      }
 *          (3)
 *          (1)
 *          (1)
 */

@Configuration
@EnableAspectJAutoProxy
public class MainConfigOfAOP {
    @Bean
    public MathCalculator mathCalculator() {
        return new MathCalculator();
    }

    @Bean
    public LogAspects logAspects() {
        return new LogAspects();
    }
}
