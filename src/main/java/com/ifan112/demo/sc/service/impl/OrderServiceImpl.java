package com.ifan112.demo.sc.service.impl;

import com.ifan112.demo.sc.service.OrderService;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Service;

// @Service
// @Conditional(OrderServiceCondition.class)
public class OrderServiceImpl implements OrderService {

    public OrderServiceImpl() {
        System.out.println("构造OrderServiceImpl");
    }

    @Override
    public void newOrder(String username, long goodId) {
        System.out.println("----- 创建订单 ------");
        System.out.println(username);
        System.out.println(goodId);
    }
}

class OrderServiceCondition implements ConfigurationCondition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // 这里简单点返回false，表示任何条件都不满足，不要注册该bean
        return false;
    }

    @Override
    public ConfigurationPhase getConfigurationPhase() {
        // 判断条件发生的阶段，这里是在配置阶段判断是否满足条件
        return ConfigurationPhase.PARSE_CONFIGURATION;
    }
}