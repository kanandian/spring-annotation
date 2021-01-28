package com.flyer.condition;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

// 判断是否是linux操作系统
public class LinuxCondition implements Condition {
    /**
     * 参数讲解：
     * conditionContext：判断条件能使用的上下文
     * annotatedTypeMetadata：注释信息
     *
     *
     * @param conditionContext
     * @param annotatedTypeMetadata
     * @return
     */
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        // 判断是否是linux系统
        // 能获取到ioc容器使用的beanFactory（用于创建bean）
        ConfigurableListableBeanFactory beanFactory = conditionContext.getBeanFactory();
        // 获取类加载器
        ClassLoader classLoader = conditionContext.getClassLoader();
        // 获取当前环境信息
        Environment environment = conditionContext.getEnvironment();
        // registry用于注册bean
        BeanDefinitionRegistry registry = conditionContext.getRegistry();

        // 判断容器中是否已经注册过id为bill的实例
//        registry.containsBeanDefinition("bill");

        // 获取操作系统名字
        String osName = environment.getProperty("os.name");
        if (osName.toLowerCase().contains("linux")) {
            return true;
        }
        return false;
    }
}
