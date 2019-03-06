package com.ifan112.demo.sc.service;

/**
 * ApplicationContextAware接口 和 该接口的处理器ApplicationContextAwareProcessor 的用法示例服务接口。
 */
public interface ApplicationContextAwareService {

    /**
     * 获取context中所有注册的bean的总个数
     *
     * @return 所有注册的bean的总个数
     */
    int getAllBeanDefinitions();
}
