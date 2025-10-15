package com.myspring.entity;

import com.myspring.annotation.Component;
import com.myspring.configuration.InitializingBean;

@Component
public class Plane implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Plane bean init");
    }
}
