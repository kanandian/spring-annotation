package com.flyer.config;

import com.flyer.bean.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
// 使用@PropertiesSource读取外部配置文件中属性（key、value）保存到运行的环境变量中（Environment）,保存后可以用${key}来取出配置文件中的值
@PropertySource(value = {"classpath:/person.properties"})
public class MainConfigOfPropertyValues {
    @Bean
    public Person person() {
        return new Person();
    }
}
