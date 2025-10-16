package com.myspring.entity;

import com.myspring.annotation.Component;
import com.myspring.configuration.DisposableBean;
import com.myspring.configuration.InitializingBean;

import java.lang.invoke.MethodHandles;

@Component
public class Plane implements InitializingBean, DisposableBean {
    @Override
    public void destroy() {
        System.out.println("Plane очищен через DisposableBean");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Method afterPropertiesSet() in " + MethodHandles.lookup().lookupClass().getSimpleName() + " was called");
    }
}
