package com.ifan112.demo.sc;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * context事件监听器
 *
 * 该监听器上的@Component注解表明其实一个组件，它将会被context管理并初始化。
 *
 * 但它是一个特殊的组件，用于监听context事件的监听器。
 *
 * 在它被初始化之后，由{@link org.springframework.context.support.ApplicationListenerDetector}将其识别出来，
 * 并且添加到context事件监听器列表中。
 *
 * 此后，当context发布事件之后，该监听器就可以收到发布的事件了。
 *
 * 通常context会有以下类型的事件：
 * 1. 在context刷新时的 {@link org.springframework.context.event.ContextRefreshedEvent} 事件
 * 2. 在context启动时的 {@link org.springframework.context.event.ContextStartedEvent} 事件
 * 3. 在context停止时的 {@link org.springframework.context.event.ContextStoppedEvent} 事件
 * 4. 在context关闭时的 {@link org.springframework.context.event.ContextClosedEvent} 事件
 */

@Component
public class ApplicationContextEventListener implements ApplicationListener<ApplicationEvent> {

    public ApplicationContextEventListener() {
        System.out.println("初始化事件监听器");
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println("接收到事件 " + event);
    }
}
