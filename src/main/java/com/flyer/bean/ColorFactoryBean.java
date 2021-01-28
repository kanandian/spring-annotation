package com.flyer.bean;

import org.springframework.beans.factory.FactoryBean;

// FactoryBean获取的是getObject()方法返回的实例的类型
public class ColorFactoryBean implements FactoryBean<Color> {
    // 返回一个Color对象，这个对象会添加到容器
    public Color getObject() throws Exception {
        return new Color();
    }

    public Class<?> getObjectType() {
        return Color.class;
    }

    public boolean isSingleton() {
        return true;
    }
}
