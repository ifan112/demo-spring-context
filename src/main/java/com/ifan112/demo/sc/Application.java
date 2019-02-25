package com.ifan112.demo.sc;

import org.springframework.context.annotation.*;

@Configuration
@ComponentScan("com.ifan112.demo.sc")
public class Application {

    public static void main(String[] args) {

        // 1. ClassPathXmlApplication 或 FileSystemXmlApplicationContext 通过读取并解析xml文件来初始化spring容器
        // 与AnnotationConfigApplicationContext相比，它们不必解析注解的配置类或者扫描包，所有关于bean的配置都在xml中声明
        // ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:application-context.xml");

        // 2. 构造AnnotationConfigApplicationContext时传入注解的配置类或者扫描包参数，直接初始化spring容器
        // 2.1
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Application.class);
        // 2.2
        // AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.king.onlyone");

        // 3. 构造AnnotationConfigApplicationContext，不传入任何参数
        // 此后，通过register(注解的配置类)和scan(包)和调用refresh()方法来初始化该spring容器
        // AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        // 3.1 context.register(Application.class);
        // context.refresh();

        // 3.2 context.scan("com.king.onlyone");
        // context.refresh();




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

    }
}
