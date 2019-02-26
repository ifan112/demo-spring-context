package com.ifan112.demo.sc;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;


/**
 * OrderServiceImpl实现ApplicationContextAware接口，表明该bean希望获取到ApplicationContext实例。
 * 那么，获取的合适时机就是在该bean被创建之后。
 *
 * 该bean在创建之后，bean的后置处理器ApplicationContextAwareProcessor会检查该bean是否实现了
 * ApplicationContextAware接口，如果实现了该接口，那么将促使该bean调用接口的方法，以实现自定义的设置。
 *
 * 参考：{@link org.springframework.context.support.ApplicationContextAwareProcessor#invokeAwareInterfaces(Object)}
 *
 * 然而，由于该类与spring的接口有耦合，因此实现接口并不是一种很好的处理方式。
 *
 * 可以在context上加入@Autowired，让spring在创建该bean之后，自动将ApplicationContext的依赖织入进来。
 * 这样，就不必实现ApplicationContextAware接口了。解除了与spring的耦合。
 */

@Service
public class ApplicationContextAwareServiceImpl implements ApplicationContextAwareService, ApplicationContextAware {
// 不实现ApplicationContextAware接口
// public class ApplicationContextAwareServiceImpl implements ApplicationContextAwareService {

    /**
     * 可以不实现ApplicationContextAware接口，而在该字段上加上@Autowire，让spring自动注入ApplicationContext实例
     */
    @Autowired
    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // OrderServiceImpl在创建之后，该方法将会得到回调，以对该bean实现一些自定义设置
        // 这里简单的保存一下ApplicationContext
        this.context = applicationContext;
    }


    @Override
    public int getAllBeanDefinitions() {
        return context.getBeanDefinitionCount();
    }
}
