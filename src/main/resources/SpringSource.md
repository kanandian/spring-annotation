# Spring容器的创建过程
* ### 容器的创建以及刷新(容器的refresh()方法) (BeanFactory的创建以及与准备工作)
    1. prepareRefresh()：刷新前的预处理
        * initPropertySources()：初始化一些属性设置，子类自定义化的属性设置方法
            (空白方法，自定义时才有用)
        * getEnvironment().validateRequiredProperties(): 进行属性校验
        * this.earlyApplicationEvents = new LinkedHashSet<ApplicationEvent>()：保存早期的事件，先做保存，等事件派发器初始化完成后，由其进行派发
    2. obtainFreshBeanFactory()：获取BeanFactory
        * refreshBeanFactory(): 创建BeanFactory
            * 创建了一个BeanFactory：this.beanFactory = new DefaultListableBeanFactory()
            * 设置一个序列化id
        * getBeanFactory()：返回刚才创建的BeanFactory
    3. prepareBeanFactory(beanFactory)：对BeanFactory进行预准备工作（进行一些设置）
        * 设置BeanFactory的类加载器、表达式解析器...
        * 添加部分ApplicationContextAwareProcessor(BeanPostProcessor)
        * 设置忽略的自动装配的接口：无法通过接口类型自动注入的接口(EnvironmentAware、EmbeddedValueResolverAware、...)
        * 注册可以解析的自动装配：我们能直接在任何组件中自动注入的(BeanFactory、ResourceLoader、ApplicationEventPublisher、ApplicationContext)
        * 添加ApplicationListenerDetector(BeanPostProcessor)
        * 添加编译时的AspectJ支持
        * 向BeanFactory注册一些环境设置：
            * environment（ConfigurableEnvironment）
            * systemProperties(Map<String, Object>)
            * systemEnvironment(Map<String, Object>)
    4. postProcessBeanFactory(beanFactory)：BeanFactory准备完成后进行的后置处理工作(空白方法，为子类做准备)
        * 子类通过重写这个方法再BeanFactory创建并与准备完成后做进一步的设置


###### 以上为BeanFactory的创建以及预准备工作
---
* ### 容器的创建以及刷新(容器的refresh()方法)(剩下的内容)
    5. invokeBeanFactoryPostProcessors(beanFactory)：执行BeanFactoryPostProcessor(之前讲过)
        * 在BeanFactory标准初始化(之前的4步)之后执行的
        * 两个重要的接口
            * BeanFactoryPostProcessor
            * BeanDefinitoinRegistryPostProcessor(extends BeanFactoryPostProcessor)
        * 流程：
            1. 执行PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors())(之前讲过)
                * 获取所有的BeanDefinitoinRegistryPostProcessor
                * 按照优先级进行分类(PriorOrdered、Ordered、NonOrdered(没有实现优先级接口的BeanDefinitoinRegistryPostProcessor))
                * 依次执行BeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry()

                * BeanFactoryPostProcessor的处理与BeanDefinitoinRegistryPostProcessor一样
    6. registerBeanPostProcessors(beanFactory)：注册BeanPostProcessor(bean的后置处理器，拦截bean的创建过程)
        ###### 重要的子接口：DestructionAwareBeanPostProcessor、SmartInstantiationAwareBeanPostProcessor、InstantiationAwareBeanPostProcessor、MergedBeanDefinitionPostProcessor（不同的BeanPostProcessor在bean创建前后执行的时机是不一样的）
        * PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, this)
            1. 获取所有的BeanPostProcessor
            2. 按照优先级进行分类(PriorOrdered、Ordered、NonOrdered)(与BeanFactoryPostProcessor的处理类似，后置处理器都有优先级)(注意：MergedBeanDefinitionPostProcessor会同时会被放在priorityOrderedPostProcessors和internalPostProcessors)
            3. 按照顺序注册BeanPostProcessor
                * registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors) -> beanFactory.addBeanPostProcessor(postProcessor)(for遍历注册)
            4. 然后重新注册internalPostProcessors(MergedBeanDefinitionPostProcessor)
            5. 最后注册了一个ApplicationListenerDetector：在bean创建后检查是否是ApplicationListener，如果是，则向容器中注册监听器：applicationContext.addApplicationListener((ApplicationListener<?>) bean);
    7. initMessageSource()：初始化MessageSource组件（国际化功能、消息解析、消息绑定）
        1. 获取BeanFactory
        2. 判断容器中是否有id为messageSource的组件(自定义的MessageSource)
            若有，直接获取
            若没有，则创建一个若默认的MessageSource(DelegatingMessageSource)，然后注册到BeanFactory，以后可以自动注入
            ###### MessageSource: 取出国际化配置文件中的某个key值，能按照区域信息
    8. initApplicationEventMulticaster()：初始化事件派发器
        1. 获取BeanFactory
        2. 判断容器中是否有事件派发器(自定义的事件派发器)
            若配置了，则直接从beanFactory中获取
            否则，创建一个默认事件派发器SimpleApplicationEventMulticaster，然后在容器中注册，以后其他组件可以自动注入
    9. onRefresh(): 向子容器中初始化特殊的bean(空白方法，留给容器的子类实现)
        * 子类重写这个方法，在容器刷新的时候可以实现自定义逻辑
    10. registerListeners()：将ApplicationListener注册到容器
        1. 从容器中拿到所有的ApplicationListener组件：getBeanNamesForType(ApplicationListener.class, true, false)
        2. for遍历添加ApplicationListener到派发器中
        3. for遍历之前保存的早期ApplicationEvent(earlyApplicationEvents：ApplicationListener创建之前产生的产生的事件(#1中提到过)), 然后分别派发事件
    11. finishBeanFactoryInitialization(beanFactory)：初始化所有剩下的单实例bean(non-lazy)
        * beanFactory.preInstantiateSingletons()：初始化剩下的单实例bean
            1. 获取容器中所有的bean，依次进行依次创建和初始化bean
            2. 获取bean的定义信息(RootBeanDefinition)
            3. 判断该bean不是抽象的，是单例的，不是懒加载的
                * 返回true：
                    1. 判断是否是FactoryBean(该bean是否实现FactoryBean接口)
                        * 返回true：
                        * 返回false：利用getBean()方法创建对象
                            1. getBean() -> doGetBean()
                            2. 先获取缓存(Map<String, Object> singletonObjects)中保存的单实例bean，若还能获取到说明之前已经创建过(所有创建过的单实例bean都会被缓存起来)
                            3. 若缓存中获取不到，则开始bean的创建流程
                            4. 标记当前bean已被创建，防止多线程重复创建的问题
                            5. 获取bean的定义信息(RootBeanDefinition mbd)
                            6. 获取当前bean依赖的其他bean(mbd.getDependsOn())，并for遍历创建这些bean(调用getBean()方法)
                            7. 启动单实例bean创建流程ObjectFactory.getObject() -> createBean()
                                1. 解析bean的类型Class<?>
                                2. Object bean = resolveBeforeInstantiation(beanName, mbdToUse): 给BeanPostProcessor(InstantiationAwareBeanPostProcessor)一个机会返回bean的代理对象(该方法的逻辑之前讲过)
                                ``` java
                                    bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
					                if (bean != null) {
						                bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
					                }
                                ```
                                (注意：其他BeanPostProcessor都是在bean创建完成后，调用初始化方法前后进行拦截，而InstantiationAwareBeanPostProcessor是在bean创建之前进行拦截，返回bean代理对象)
                                3. 若上一步没有返回代理对象，则自己创建bean对象Object beanInstance = doCreateBean(beanName, mbdToUse, args)
                                    1. instanceWrapper = createBeanInstance(beanName, mbd, args)：创建bean的实例
                                        * 利用工厂方法(@Bean标注的也算是工厂方法)或者构造器创建bean实例
                                    2. applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
                                        * 遍历所有的BeanPostProcessor，对所有的MergedBeanDefinitionPostProcessor调用其postProcessMergedBeanDefinition()方法
                                    3. populateBean(beanName, mbd, instanceWrapper): 为bean的属性进行赋值
                                        * 获取bean的属性值：PropertyValues pvs = mbd.getPropertyValues();
                                        * 遍历获取所有的InstantiationAwareBeanPostProcessor并调用其postProcessAfterInstantiation()方法
                                        * 遍历获取所有的InstantiationAwareBeanPostProcessor并调用其postProcessPropertyValues()方法
                                        * 应用bean的属性值applyPropertyValues(beanName, mbd, bw, pvs)： 利用反射调用bean的setter方法进行赋值
                                    4. bean初始化exposedObject = initializeBean(beanName, exposedObject, mbd)
                                        * __invokeAwareMethods(beanName, bean)： 执行XXXAware接口的方法(BeanNameAware、BeanClassLoaderAware、BeanFactoryAware)__
                                        * wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName); 
                                        * invokeInitMethods(beanName, wrappedBean, mbd);
                                            * 之前讲过很多初始化方法的指定方式，这里根据不同的方式分别调用初始化方法
                                        * wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
                                    5. 注册bean的销毁方法(容器销毁以后调用)：registerDisposableBeanIfNecessary(beanName, bean, mbd);
                                4. bean创建完成后将其添加到缓存(singletonObjects)中: addSingleton(beanName, singletonObject);
                                * __IOC容器就是这些Map，各种各样的Map保存着单实例bean、环境信息...，从容器中获取组件、信息，就是从这些Map中获取__
                            
                * 返回false：
        * 所有bean都利用getBean()方法创建完成后，for检查所有bean是否是SmartInitializingSingleton接口的，如果是，则执行afterSingletonsInstantiated()方法__之前讲@EventListener原理是讲到过__
    12. finishRefresh()：完成BeanFactory的初始化创建工作，IOC容器创建完成
        1. initLifecycleProcessor()：初始化生命周期有关的后置处理器(LifecycleProcessor接口(容器中只能有一个实现类))，若容器中没有，则创建一个默认的生命周期组件DefaultLifecycleProcessor，并添加到容器中
            * 可以写一个实现类，在容器刷新和关闭时调用其方法onRefresh()和onClose()方法
        2. 触发生命周期组件(前一步初始化的)的onfresh()方法：getLifecycleProcessor().onRefresh();
        3. 发布容器刷新完成事件ContextRefreshedEvent：publishEvent(new ContextRefreshedEvent(this));(之前讲过)
        4. LiveBeansView.registerApplicationContext(this);

***
# 总结 Spring源码的核心
1. Spring容器在启动时，先会保存所有注册进来的bean定义信息
    * xml注册bean：<bean>标签
    * 使用注解注册bean：@Service、@Component、@Bean、...
2. Spring容器在合适的时机创建这些bean
    * 时机：
        1. 用到这个bean的时候，利用getBean()方法创建，并保存在容器中
            * 统一创建bean之前要用到
            * 不是单实例bean
        2. 统一创建bean的时候：finishBeanFactoryInitialization(beanFactory);
3. 后置处理器：
    * 每一个bean创建完之后都会使用各种后置处理器(BeanPostProcessor)进行处理，来增强bean的功能，例如
        * AutowiredAnnotationBeanPostProcessor：处理自动注入功能
        * AnnotationAwareAspectJAutoProxyCreator：实现AOP功能
        * 增强功能注解
        * ...
4. 事件驱动模型：
    * ApplicationListener：事件监听
    * ApplicationEventMulticaster：事件派发

    
    


    