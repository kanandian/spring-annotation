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
 *          (2) createBean(): 创建Bean   InstantiationAwareBeanPostProcessor会在任何bean创建之前尝试返回bean的代理实例(因此AnnotationAwareAspectJAutoProxyCreator在任何bean创建之前都会进行拦截，返回一个代理bean实例，以实现aop的功能)
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
 *                   (2) 内容见后AnnotationAwareAspectJAutoProxyCreator部分
 *
 *
 * AnnotationAwareAspectJAutoProxyCreator(implements InstantiationAwareBeanPostProcessor)(上一个部分resolveBeforeInstantiation(beanName, mbdToUse)方法中的内容)
 *  1. 每一个bean创建之前调用postProcessBeforeInstantiation()方法
 *      现在关心MathCalculator和LogAspect的创建
 *          (1) 判断当前bean是否在advisedBeans(保存着需要增强的bean)中
 *          (2) 判断当前bean是否是基础类型(Advice、Pointcut、Advisor、AopInfrastructureBean)或者是切面(是否标注了@Aspect)，是否需要跳过
 *              如何判断是否跳过：
 *                  1. 获取候选的增强器(切面里的通知方法)：List<Advisor> candidateAdvisors = this.findCandidateAdvisors();
 *                      每一个增强器的类型是InstantiationModelAwarePointcutAdvisor
 *                  2. 判断每一个增强器是否是AspectJPointcutAdvisor类型的，若是：返回true 否则：返回false

 *  2. 创建对象
 *  3. 每一个bean创建之后调用postProcessAfterInitialization(result, beanName)
 *      调用postProcessAfterInitialization()方法
 *          return this.wrapIfNecessary(bean, beanName, cacheKey); 如果需要就进行包装
 *              (1) 获取当前bean的可以增强器增强器(通知方法) Object[] specificInterceptors
 *                  先获取所有的增强器，然后找出当前bean可用的增强器，并对可用增强器进行排序
 *              (2) 保存当前bean到advisedBeans中，表示当前bean已经被增强处理
 *              (3) 如果当前bean需要增强，则创建代理对象    Object proxy = this.createProxy(bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
 *                  1. 获取可用增强器，保存到proxyFactory  proxyFactory.addAdvisors(advisors);
 *                  2. 创建代理对象：Spring自行决定创建哪个
 *                      new ObjenesisCglibAopProxy(config)：cglib动态代理
 *                      new JdkDynamicAopProxy(config))：jdk动态代理
 *              (4) 向容器中返回当前组件使用cglib增强了的代理对象（以后容器中拿到的就是这个组件的代理对象，执行目标方法时，代理对象会执行通知方法的流程）
 *
 * =====================代理对象方法的执行流程=============================
 * 容器中保存的是组件的代理对象（cglib增强后的对象），保存着详细信息（比如增强器、目标对象。。。）
 *  1. 调用CglibAopProxy.intercept()方法拦截目标方法执行
 *  2. 根据ProxyFactory对象获取目标方法的拦截器链
 *      List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
 *          1. List<Object> interceptorList = new ArrayList(config.getAdvisors().length);  interceptorList用于存储拦截器链
 *              保存所有的增强器（这里一共有5个：一个默认的ExposeInvocationInterceptor和4个LogAspect切面类中定义的增强器）
 *          2. 遍历所有的增强器，将其转为Interceptor[]: registry.getInterceptors(advisor);
 *          3. 将增强器(advisor)转化为MethodInterceptor[]
 *              若advisor是MethodInterceptor，则直接添加到MethodInterceptor[]
 *              若不是则使用AdvisorAdapter适配器，将advisor包装成MethodInterceptor，然后添加
 *              转换完成，返回MethodInterceptor[]
 *
 *  3. 如果拦截器链为空，直接执行目标方法
 *      拦截器链：每一个通知方法又被包装成方法拦截器，利用MethodInterceptor机制
 *      retVal = methodProxy.invoke(target, argsToUse);
 *  4. 如果有拦截器链，创建一个CglibMethodInvocation对象(需要传入目标对象、目标方法、拦截器链等信息)，并调用proceed()方法触发拦截器链
 *      retVal = (new CglibAopProxy.CglibMethodInvocation(proxy, target, method, args, targetClass, chain, methodProxy)).proceed();
 *  5. 拦截器链的触发过程(责任链模式): 调用CglibMethodInvocation.proceed()方法
 *      if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) { // currentInterceptorIndex初始值为-1
 *          return this.invokeJoinpoint();  // 若拦截器链中没有拦截器，或拦截器索引来到最后一个拦截器，则直接使用反射调用目标方法
 *     } else {
 *          // 这里处理拦截器
 *          。。。
 *          return ((MethodInterceptor)interceptorOrInterceptionAdvice).invoke(this);
 *          这里invoke方法中再调用CglibMethodInvocation.proceed()完成责任链闭环，并在合适的位置调用拦截器，这里注意各个拦截器的顺序
 *          使用拦截器链的方式保证通知方法和目标方法的执行顺序
 *     }
 *
 * 总结：
 *  1. 使用@EnableAspectJAutoProxy注解开启AOP功能(该注解向容器中注册一个组件AnnotationAwareAspectJAutoProxyCreator)
 *  2. AnnotationAwareAspectJAutoProxyCreator是一个后置处理器
 *  3. 容器创建流程：
 *      (1) registerBeanPostProcessors(beanFactory): 向容器中注册后置处理器(在这里创建并注册AnnotationAwareAspectJAutoProxyCreator对象)
 *      (2) finishBeanFactoryInitialization(beanFactory)：初始化剩下的单实例bean
 *          1. 创建业务逻辑和切面组件
 *          2. AnnotationAwareAspectJAutoProxyCreator拦截组件的创建过程
 *          3. 组件创建完成之后判断组件是否需要增强
 *              是：将切面的通知方法包装成增强器(Advisor)，给业务组件创建一个代理对象
 *      (3) 执行目标代码
 *          1. 使用代理对象执行目标方法
 *          2.  CglibAopProxy.intercept()方法拦截并执行目标方法
 *              (1) 得到目标方法的拦截器链(增强器包装成拦截器(MethodInterceptor))
 *              (2) 利用拦截器的链式机制(责任链模式)依次进入每一个拦截器执行
 *              效果：
 *               正常执行：前置通知->目标方法->后置通知->返回通知
 *               异常执行：前置通知->目标方法->后置通知->异常通知
 *
 *
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
