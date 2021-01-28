package com.flyer.config;

import com.flyer.bean.Color;
import com.flyer.bean.ColorFactoryBean;
import com.flyer.bean.Person;
import com.flyer.condition.LinuxCondition;
import com.flyer.condition.MyImportBeanDefinitionRegistrar;
import com.flyer.condition.MyImportSelector;
import com.flyer.condition.WindowsCondition;
import org.springframework.context.annotation.*;

//@Conditional({})  // 当该注解配在类上时，表示满足当前条件，该配置类中所有的bean才会创建（类中组件统一设置）
@Configuration
@Import({Color.class, MyImportSelector.class, MyImportBeanDefinitionRegistrar.class})    // 用于快速导入组件，id默认是类的全类名（可以是数组）（更快速轻量）
public class MainConfig2 {

    /**
     * @Scope()注解的值：
     * prototype: 多实例的：只有要获取实例时才会调用@Bean方法创建对象，且获取几次就调用几次
     * singleton: 单实例的(默认值)：ioc容器启动时会调用@Bean的方法创建对象，放入容器
     * 以下是web环境下独有的，却一般用不到:
     * request: 同一个请求（request）创建一个实例
     * session: 同一个session创建一个实例
     * @return
     */

    /**
     * 懒加载：
     * 单实例bean默认在容器启动的时候创建对象
     * 懒加载：容器启动时先不创建对象，在第一次使用时才创建，并初始化
     * @return
     */

    @Bean
    @Lazy   // 懒加载：容器启动时先不创建对象，在第一次使用时才创建，并初始化
//    @Scope("prototype") // 该注解相当于bean标签中的scope属性
    public Person person() {
        return new Person("wangwu", 27);
    }


    /**
     * @Conditional(Condition[]): 按照一定的条件进行判断，满足条件给容器中注册bean
     */

    @Bean("bill")
    @Conditional({WindowsCondition.class})
    public Person person1() {
        return new Person("Bill Gates", 62);
    }

    @Bean("linus")
    @Conditional({LinuxCondition.class})
    public Person person2() {
        return new Person("Linus", 48);
    }

    // FactoryBean获取的是getObject()方法返回的实例的类型
    @Bean
    public ColorFactoryBean color() {
        return new ColorFactoryBean();
    }


    /**
     * 总结：（向容器中注册组件的方式）
     * 1. CompontScan+组件标注注解（包括：@Controller、@Service、@Repository、@Component）（适用于自己开发的程序）
     * 2. @Bean注解(适用于引用第三方包，没有加入以上注解的情况)
     * 3. @Import:
     *  (1) 用于快速导入组件，id默认是类的全类名（可以是数组）（更快速轻量）
     *  (2) ImportSelector: 返回需要导入的组件的全类名的数组
     *  (3) ImportBeanDefinitionRegistrar: 手动注册bean到容器中（一个或多个）
     * 4. 使用Spring提供的FactoryBean（工程Bean）
     *  FactoryBean获取的是getObject()方法返回的实例的类型,若要获取到FactoryBean本身，在id前面加"&"
     */
}
