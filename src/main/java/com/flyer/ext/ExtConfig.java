package com.flyer.ext;

import com.flyer.bean.Car;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 * 扩展原理
 *
 * 1. BeanPostProcessor: bean后置处理器，bean创建对象初始化前后进行拦截工作
 * 2. BeanFactoryPostProcessor: BeanFactory的后置处理器
 *  在BeanFactory标准初始化调用之后调用，来修改和定制BeanFactory中的内容
 *  (此时所有的bean定义已经保存加载了，但是bean实例还未创建)
 *
 *  具体过程(AbstractApplicationContext.refresh()方法中)
 *      1. 使用invokeBeanFactoryPostProcessors(beanFactory)方法进行调用
 *          (1) 在BeanFactory中找到所有的BeanFactoryPostProcessor String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);
 *          (2) 根据PriorityOrdered、Ordered、NonOrdered进行分类(与BeanPostProcessor类似)
 *          (3) 依次调用他们的postProcessBeanFactory()方法
 *
 *  在refresh()中各方法的调用顺序可知BeanFactoryPostProcessor的执行时机
 *
 * 3. BeanDefinitionRegistryPostProcessor(extends BeanFactoryPostProcessor)
 *  额外定义了postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)方法
 *      在所有bean定义信息将要被加载，但bean实例还未被创建的时候执行
 *      (这里BeanDefinitionRegistry里保存着每一个bean的定义信息，以后BeanFactory就是按照BeanDefinitionRegistry中保存着的每一个bean的定义信息创建bean实例的)
 *      (因此在BeanFactoryPostProcessor之前执行，可以利用postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)方法再向容器中添加一些组件)
 *
 *  原理：
 *      (1) ioc创建对象(refresh()方法)
 *      (2) invokeBeanFactoryPostProcessors(beanFactory); -> PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors()); (和上面讲的BeanFactoryPostProcessor一样)
 *          1. 先从容器中取到所有的BeanDefinitionRegistryPostProcessor：String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
 *          2. 根据PriorityOrdered、Ordered、NonOrdered进行分类(与BeanFactoryPostProcessor类似)
 *          3. 依次调用他们的postProcessBeanDefinitionRegistry方法，然后触发他们的postProcessBeanFactory方法
 *      (3) 然后才会在容器中获取到所有的BeanFactoryPostProcessor，并按照优先级依次触发其invokeBeanFactoryPostProcessors方法
 *      也就是说BeanDefinitionRegistryPostProcessor的invokeBeanFactoryPostProcessors方法要先于BeanFactoryPostProcessor的invokeBeanFactoryPostProcessors方法执行
 *  注意：BeanDefinitionRegistryPostProcessor和BeanFactoryPostProcessor的处理在同一个方法中(invokeBeanFactoryPostProcessors)，先处理BeanDefinitionRegistryPostProcessor
 *      然后处理BeanFactoryPostProcessor，因此BeanDefinitionRegistryPostProcessor的处理要先于BeanFactoryPostProcessor的方法
 *
 *
 * 4. ApplicationListener <E extends ApplicationEvent> extends EventListener: 监听容器中发布的事件，完成事件驱动
 *  监听ApplicationEvent及其子类
 *      步骤：
 *          (1) 写一个监听器(ApplicationListener的实现类(MyApplicationListener类中) 或 @EventListener(UserService类中，下面解释原理))来监听某个事件(ApplicationEvent及其子类)
 *          (2) 将监听器放到容器中
 *          (3) 只要容器中有相关事件的发布，我们就能监听到这个事件，并调用方法
 *              ContextRefreshedEvent：容器刷新完成，(所有bean创建完成)会发布这个事件
 *              ContextClosedEvent：关闭容器会发布这个事件
 *          (4) 如何发布事件：使用applicationContext.publishEvent()方法
 *      原理：（Spring内部在不同条件下publishEvent不同的事件）
 *          (1) ContextRefreshedEvent
 *              1. refresh()
 *              2. finishRefresh(); (refresh()方法最后调用的方法)
 *              3. publishEvent(new ContextRefreshedEvent(this));
 *                  事件发布流程：
 *                      (1) 获取事件多播器(派发器)getApplicationEventMulticaster().multicastEvent(applicationEvent, eventType);
 *                      (2) multicastEvent()方法派发事件
 *                          获取所有的ApplicationListener，遍历并调动其监听方法
 *                          for (final ApplicationListener<?> listener : getApplicationListeners(event, type)) {
 * 			                    如果有Excutor，可以使用Excutor异步开发
 * 			                    否则同步方式直接执行listener方法 invokeListener(listener, event) -> doInvokeListener(ApplicationListener listener, ApplicationEvent event) -> listener.onApplicationEvent(event);
 *          (2) 自己发布的事件，发布流程和ContextRefreshedEvent的publishEvent()方法一样
 *          (3) ContextClosedEvent:
 *              applicationContext.close() -> doClose() -> publishEvent(new ContextClosedEvent(this)) -> ... (后面一致)
 *
 *      事件多播器(派发器)的获取
 *          (1) 容器创建对象(refresh()方法)
 *          (2) initApplicationEventMulticaster(): 初始化ApplicationEventMulticaster
 *              1. 如果容器中有id=applicationEventMulticaster的组件，则直接beanFactory.getBean()获取组件
 *                  if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
 *                      ...
 *                  }
 *              2. 若没有，则创建简单的ApplicationEventMulticaster：new SimpleApplicationEventMulticaster(beanFactory)
 *                  然后在容器中注册，这样以后其他组件需要派发事件时，可以自动注入ApplicationEventMulticaster
 *                  this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
 * 			        beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
 *
 *      容器中有哪些监听器(getApplicationListeners(event, type)):
 *      注册监听器：
 *          1. refresh()方法中调用registerListeners()注册监听器
 *          2. 从容器中获取到所有的监听器，并将他们注册到ApplicationEventMulticaster中
 *              String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
 * 		        for (String listenerBeanName : listenerBeanNames) {
 * 		            // 这里进行注册
 * 			        getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
 *              }
 *
 *      可以自定义ApplicationEventMulticaster，并指定Executor(SyncTaskExecutor(同步)、AsyncTaskExecutor(异步))
 *
 *      @EventListener原理： 利用EventListenerMethodProcessor(implements SmartInitializingSingleton)处理器解析方法上的@EventListener
 *          SmartInitializingSingleton原理：
 *              1. ioc容器创建对象refresh()
 *              2. finishBeanFactoryInitialization(beanFactory): 初始化剩下的单实例bean(创建bean的那个方法)
 *              3. beanFactory.preInstantiateSingletons(): 初始化剩下的单实例bean(创建bean的那个方法)
 *                  在这个方法中，有之前提到过的创建单实例bean的逻辑(for循环 getBean()方法)
 *                  创建完所有单实例bean之后，遍历(for循环)，对所有SmartInitializingSingleton子类，调用afterSingletonsInstantiated()方法
 */

@Configuration
@ComponentScan("com.flyer.ext")
public class ExtConfig {
    @Bean
    public Car car() {
        return new Car();
    }
}
