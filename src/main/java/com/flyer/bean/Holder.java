package com.flyer.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// 默认加在ioc容器中的组件，容器启动会调用无参构造器，在进行初始化赋值等操作
@Component
public class Holder {
    private Car car;

    public Holder() {
        System.out.println("Holder 无参构造器");
    }

//    @Autowired
    // 标注在构造器上，作用类似标注在方法上的@Autowired，若只有一个有参构造器，则可以省略@Autowired
    public Holder(Car car) {
        this.car = car;
        System.out.println("holder 有参构造器");
    }

    public Car getCar() {
        return car;
    }

//    @Autowired
    // 标注在方法，Spring容器创建当前对象，就会调用该方法，完成赋值
    // 方法使用的参数，会从ioc容器中获取
    public void setCar(@Autowired Car car) {
        this.car = car;
    }

    @Override
    public String toString() {
        return "Holder{" +
                "car=" + car +
                '}';
    }
}
