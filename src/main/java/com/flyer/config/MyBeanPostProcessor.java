package com.flyer.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

// 在bean的初始化前后进行一些处理操作
@Component  // 将后置处理器加入到容器中
public class MyBeanPostProcessor implements BeanPostProcessor {
    /**
     * @param bean: 刚创建调用了构造器而调用初始化方法的bean
     * @param beanName: bean对应的id
     * @return 返回一个后面要用的bean实例，可以直接返回bean，也可以包装一下再返回（代理）
     * @throws BeansException
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("postProcessBeforeInitialization   " + beanName + "  ->  " + bean);
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("postProcessAfterInitialization   " + beanName + "  ->  " + bean);
        return bean;
    }
}
