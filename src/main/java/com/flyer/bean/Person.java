package com.flyer.bean;

import org.springframework.beans.factory.annotation.Value;

public class Person {
    /**
     * 使用@Value进行赋值（与beans.xml中属性赋值方式对应）
     * 1. 基本类型
     * 2. SpEL：#{}
     * 3. 使用配置文件(properties文件)中的值：${} (配置文件中的值会放在Environment中)（需要在配置类中使用@PropertySource指定属性来源）
     */

    @Value("張三")
    private String name;
    @Value("#{20 - 2}")
    private int age;

    @Value("${person.nickName}")
    private String nickName;

    public Person() {
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}
