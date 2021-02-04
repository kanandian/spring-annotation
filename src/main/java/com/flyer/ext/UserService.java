package com.flyer.ext;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @EventListener(classes = {ApplicationEvent.class})
    public void listener(ApplicationEvent applicationEvent) {
        System.out.println("UserService监听到事件：" + applicationEvent);
    }
}
