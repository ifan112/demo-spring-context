package com.ifan112.demo.sc;

import org.springframework.stereotype.Service;

/**
 * TestServiceImpl是一个没有实现接口的组件，context将会以cglib的方式生成对该类的对象的代理
 */

@Service
public class TestServiceImpl {

    public TestServiceImpl() {
        System.out.println("TestServiceImpl");
    }

    public void test() {
        System.out.println("TestServiceImpl-test");
    }

}
