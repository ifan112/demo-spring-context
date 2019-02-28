package com.ifan112.demo.sc;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 应用切面的配置
 *
 * @Aspect 表明这是一个Aspect的配置类，而非普通的组件。
 *         spring将会对该类进行特殊解析，以读取有关切点、切面的配置。
 *
 * @Component 表明这是一个可被context扫描并管理的组件。这个注解不可缺少。
 *
 */

@Aspect
@Component
public class ApplicationAspectConfiguration {

    /**
     * 定义切点。
     *
     * 切入点是当前包下所有的类名以ServiceImpl结尾的bean下的所有方法
     */
    @Pointcut("execution(* com.ifan112.demo.sc.*ServiceImpl.*(..))")
    public void servicePointCut() {}


    /**
     * 定义advice，即在切入点前后需要执行的增强操作
     *
     * @param point
     * @return
     */
    @Around("servicePointCut()")
    public Object logArgs(ProceedingJoinPoint point) {
        System.out.println(point.getSignature().getName() + "方法开始执行！！参数：" + Arrays.toString(point.getArgs()));
        try {
            Object result = point.proceed();
            System.out.println(point.getSignature().getName() + "方法执行结束！！结果：" + result);
            return result;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    @AfterReturning(value = "servicePointCut()", returning = "retVal")
    public void afterReturning(Object retVal) {
        System.out.println("afterReturning --- " + retVal);
    }
}
