package com.ifan112.demo.sc.service.impl;

import com.ifan112.demo.sc.service.OrderService;
import org.springframework.beans.factory.FactoryBean;

/**
 * 用于创建OrderService实例的工厂类
 *
 * 通常，我们会将FactoryBean以它所要创建的Bean名称注册到context中。
 * 例如，将OrderServiceFactoryBean以orderService这个名称注册到context。
 *
 * 当从context中以下面两种方式获取OrderService的实现时，context将会
 * 首先获取到OrderServiceFactoryBean的对象，在检测到该对象是一个FactoryBean后，
 * context将会调用getObject()方法以获取到真正的OrderServiceImpl对象。
 *
 * 1. OrderService orderService = context.getBean("orderService");
 * 2. OrderService orderService = context.getBean(OrderService.class);
 *
 * 当然，还可以直接将OrderService作为被依赖组件，注入到其它组件中。
 *
 *
 * 可以通过下面两种方式获取OrderServiceFactoryBean对象：
 * 1. OrderServiceFactoryBean orderServiceFactory = context("&orderService");
 * 2. OrderServiceFactoryBean orderServiceFactory = context(OrderServiceFactoryBean.class);
 *
 *
 * FactoryBean是spring提供的一种创建Bean的机制，通常用于下面的情况：
 * 1. 目标Bean无法通过spring自动注入依赖完成创建
 * 2. 目标Bean包含在外部jar中无法直接修改
 *
 * 例如，在mybatis库中，一个很重要的组件是SqlSessionFactory。但是，spring无法直接
 * 创建原始的SqlSessionFactory。那么，就需要使用FactoryBean的机制使得spring能够管理
 * 和创建SqlSessionFactory。详细用法参考mybatis-spring库中的SqlSessionFactoryBean。
 */

public class OrderServiceFactoryBean implements FactoryBean<OrderService> {

    @Override
    public OrderService getObject() {
        return new OrderServiceImpl();
    }

    @Override
    public Class<?> getObjectType() {
        return OrderService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void otherMethod() {
        System.out.println("我是OrderServiceFactoryBean");
    }
}
