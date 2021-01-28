package com.flyer.condition;

import com.flyer.bean.Green;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    /**
     * @param annotationMetadata    当前类的注解信息
     * @param beanDefinitionRegistry    bean注册类
     */
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        boolean isDefined = beanDefinitionRegistry.containsBeanDefinition("person");
        if (isDefined) {
            // bean的定义信息
            BeanDefinition beanDefinition = new RootBeanDefinition(Green.class);
            beanDefinitionRegistry.registerBeanDefinition("green", beanDefinition);
        }
    }
}
