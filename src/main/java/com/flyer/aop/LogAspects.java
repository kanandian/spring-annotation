package com.flyer.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

import java.util.Arrays;

@Aspect // 告诉Spring，当前类是个切面类
public class LogAspects {

    // 抽取公共的类切入点表达式
    // 1. 本类引用: 例：@Before("pointCut()")
    // 2. 其他切面类引用：@Before("com.flyer.aop.LogAspects.pointCut()")
    @Pointcut("execution(public int com.flyer.aop.MathCalculator.*(..))")
    public void pointCut(){};

    // @Before表示在方法前执行，value属性指定目标方法（切入点）
    @Before("pointCut()")
    public void logStart(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        System.out.println(joinPoint.getSignature().getName() + "除法运行，参数列表:{" + Arrays.asList(args) + "}");
    }

    // 类似@Before，*表示所有方法，..表示任意参数列表都匹配
    @After("pointCut()")
    public void logEnd(JoinPoint joinPoint) {
        System.out.println(joinPoint.getSignature().getName() + "运行结束");
    }


    // 注意：JoinPoint必须出现在参数的第一位
    @AfterReturning(value = "pointCut()", returning = "result")
    public void logReturn(JoinPoint joinPoint, Object result) {
        System.out.println(joinPoint.getSignature().getName() + "正常结束，结果:{" + result + "}");
    }

    @AfterThrowing(value = "pointCut()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Exception exception) {
        System.out.println(joinPoint.getSignature().getName() + "出现异常，异常信息:{" + exception + "}");
    }
}
