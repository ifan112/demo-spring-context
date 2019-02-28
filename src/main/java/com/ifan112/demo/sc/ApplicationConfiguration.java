package com.ifan112.demo.sc;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 应用配置类
 *
 * @Configuration 表明这是一个配置类，作为AnnotationConfigApplicationContext启动的参数，
 *                ConfigurationClassPostProcessor会解析该类，获取关于应用的配置。
 *
 * @EnableAspectJAutoProxy 启动AspectJ自动代理。首先，当context解析到该注解后，明白开发者是要启动AspectJ和AOP相关功能。
 *               然后，注册一个AnnotationAwareAspectJAutoProxyCreator到beanFactory中。该类是一个bean初始化后置处理器，
 *               在bean初始化之后，读取@AspectJ注解有关AOP的Pointcut和Advice，将其封装成Advisor。然后，判断该bean
 *               是否有符合的切入点。如果有则对该bean进行封装，创建该类的代理类。具体来说，对于有实现接口的类则使用Jdk动态
 *               代理类进行代理，对于没有实现接口的类则使用cglib技术进行代理。并且，将advice织入到代理类中。
 *                         当该bean被作为依赖注入到其它组件，或者直接从context取出时，实际上得到的是该bean的代理对象。调用
 *               该bean的方法会变成对其代理对象的方法调用，此时，代理对象就会在调用期间根据织入的advice完成切面操作。
 *
 * @ComponentScan 设置context需要扫描的bean路径。context会调用ClassPathBeanDefinitionScanner对指定包下的class文件进行
 *               扫描，以字节流的方式，依照class文件规范读取文件内容。获取类名、父类、实现接口、注解、方法等等信息。如果该class
 *               代表的是组件，则将该bean注入到context中。此后，context将会初始化这些bean，并注入相关依赖。
 */

@Configuration
@EnableAspectJAutoProxy
@ComponentScan("com.ifan112.demo.sc")
public class ApplicationConfiguration {
}
