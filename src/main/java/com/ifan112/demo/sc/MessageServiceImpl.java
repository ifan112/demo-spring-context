package com.ifan112.demo.sc;

import org.springframework.context.annotation.*;
import org.springframework.stereotype.Service;


@Service
@Scope(value = "singleton", proxyMode = ScopedProxyMode.NO)
public class MessageServiceImpl implements MessageService {

    public MessageServiceImpl() {
        System.out.println("MessageServiceImpl实例化了");
    }

    @Override
    public boolean send(String msg) {
        System.out.println("发送消息：" + msg);
        return true;
    }

}
