// package com.ifan112.demo.sc;
//
// import org.springframework.beans.factory.FactoryBean;
//
// public class OrderServiceFactory implements FactoryBean<OrderService> {
//
//     @Override
//     public OrderService getObject() throws Exception {
//         System.out.println("创建OrderService实例");
//         return new OrderServiceImpl();
//     }
//
//     @Override
//     public Class<?> getObjectType() {
//         return OrderService.class;
//     }
//
//     @Override
//     public boolean isSingleton() {
//         return true;
//     }
// }
