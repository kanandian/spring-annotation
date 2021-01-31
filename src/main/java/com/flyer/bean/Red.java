package com.flyer.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

@Component
public class Red implements ApplicationContextAware, BeanNameAware, EmbeddedValueResolverAware {
    private String beanName;
    private ApplicationContext applicationContext;

    public void setBeanName(String beanName) {
        this.beanName = beanName;
        System.out.println("Red.beanName: " + beanName);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        System.out.println("Red.applicationContext: " + applicationContext);
    }

    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        // String值得解析器，用于解析占位符
        String value = stringValueResolver.resolveStringValue("你好，我是${os.name}，今年#{2021-2019+1}岁");
        System.out.println(value);
    }
}
