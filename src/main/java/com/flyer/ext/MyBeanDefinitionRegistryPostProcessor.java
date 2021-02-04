package com.flyer.ext;

import com.flyer.bean.Color;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("正在执行MyBeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry()方法，bean的数量：" + registry.getBeanDefinitionCount());
//        BeanDefinition beanDefinition = new RootBeanDefinition(Color.class);
        BeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(Color.class).getBeanDefinition();
        registry.registerBeanDefinition("color", beanDefinition);
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("正在执行MyBeanDefinitionRegistryPostProcessor.postProcessBeanFactory()方法, bean的数量：" + beanFactory.getBeanDefinitionCount());
    }
}
