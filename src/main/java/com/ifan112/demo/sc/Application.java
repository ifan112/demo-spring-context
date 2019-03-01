package com.ifan112.demo.sc;

import org.springframework.context.annotation.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import java.lang.reflect.Proxy;


public class Application {

    public static void main(String[] args) {

        // 1. ClassPathXmlApplication 或 FileSystemXmlApplicationContext 通过读取并解析xml文件来初始化spring容器
        // 与AnnotationConfigApplicationContext相比，它们不必解析注解的配置类或者扫描包，所有关于bean的配置都在xml中声明
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:application-context.xml");

        // 2. 构造AnnotationConfigApplicationContext时传入注解的配置类或者扫描包参数，直接初始化spring容器
        // 2.1
        // AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
        // 2.2
        // AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.king.onlyone");

        // 3. 构造AnnotationConfigApplicationContext，不传入任何参数
        // 此后，通过register(注解的配置类)和scan(包)和调用refresh()方法来初始化该spring容器
        // AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        // 3.1 context.register(Application.class);
        // context.refresh();

        // 3.2 context.scan("com.king.onlyone");
        // context.refresh();


        System.out.println("\n// ---------------------------------------- spring context ------------------------------------------ //\n");


        MessageService messageService = context.getBean(MessageService.class);
        // 对于非FactoryBean的bean，在context初始化时就会初始化
        // MessageService messageService = (MessageService) context.getBean("messageServiceImpl");
        boolean result = messageService.send("Hello World!");
        System.out.println(result);

        // FactoryBean即使是单例模式，也只有在获取的时候才会初始化
        OrderService orderService = (OrderService) context.getBean("orderServiceImpl");
        OrderService orderService2 = (OrderService) context.getBean("orderServiceImpl");
        OrderService orderService3 = (OrderService) context.getBean("orderServiceImpl");
        orderService.newOrder("用户名", 12345);

        System.out.println(orderService.hashCode());
        System.out.println(orderService2.hashCode());
        System.out.println(orderService3.hashCode());


        ApplicationContextAwareService contextAwareService = context.getBean(ApplicationContextAwareService.class);
        System.out.println("当前context中一共注册了：" + contextAwareService.getAllBeanDefinitions() + "个bean。");





        System.out.println("\n// ---------------------------------------- spring aop ------------------------------------------ //\n");

        // 获取到的是UserService代理类，具体的实现是JdkDynamicAopProxy，基于java InvokeHandler机制
        // 它代理了UserServiceImpl，并且按照配置会对UserServiceImpl的方法进行拦截操作
        UserService userService = context.getBean(UserService.class);

        Assert.isTrue(Proxy.isProxyClass(userService.getClass()), "userService并不是Jdk生成的动态代理类");

        userService.createUser("一凡", "无", 22);

        User user = userService.getUser();
        System.out.println(user);


        TestServiceImpl testService = context.getBean(TestServiceImpl.class);
        testService.test();

        // 校验testService是通过CGLIB生成的代理类
        Assert.isTrue(testService.getClass().getName().contains("CGLIB"), "");





        System.out.println("\n// ---------------------------------------- context生命周期方法 ---------------------------------------- //\n");

        // 停止context
        // 之后，可以通过即调用context.start()再次启动context
        context.stop();

        // 再次启动之前已经关闭的context
        context.start();

        MessageService secondMessageService = context.getBean(MessageService.class);
        // true。表明重新context之后，获取的bean与关闭之前的bean是同一个
        System.out.println((messageService == secondMessageService));
        // 可以正常调用服务
        boolean secondResult = secondMessageService.send("context在关闭之后又重新启动了");
        System.out.println(secondResult);


        // 关闭context
        // 对于当前不可刷新类型的AnnotationConfigApplicationContext来说，此后context不再可用
        context.close();

        // 但是，对于ClassPathXmlApplicationContext 和 AnnotationConfigWebApplicationContext
        // 这种继承自AbstractRefreshableApplicationContext的可重刷新的context来说，
        // 在关闭context之后，可以通过context.refresh()来刷新context使其可用

        // 总结来说
        // context.stop()之后，可以通过context.start()来再次启动context。此外，对于Refreshable类型的context来说，还可以通过context.refresh()来刷新context使其可用
        // context.close()之后，对于不可Refreshable类型的context来说，已经无法再次启动context了。但是，对于Refreshable类型的context来说，可以通过context.refresh()来刷新context使其可用

        // 对于Refreshable类型的context来说，不管是stop还是close当前context，总是可以通过context.refresh()来重新构造context使其可用

        // context.start()并不会刷新context内部的bean，start前后context中的bean是相同的
        // context.refresh()会刷新context内部的bean，重新解析bean定义，重新构造bean。刷新前后context中的bean是不同的

    }
}
