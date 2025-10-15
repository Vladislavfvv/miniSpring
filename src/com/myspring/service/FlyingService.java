package com.myspring.service;

import com.myspring.annotation.Autowired;
import com.myspring.annotation.Component;
import com.myspring.configuration.InitializingBean;
import com.myspring.entity.Plane;

import java.util.List;

@Component
public class FlyingService  implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("FlyingService bean init");
    }

    @Autowired
    private Plane planes;

    public Plane getPlanes() {
        return planes;
    }

    public void setPlanes(Plane planes) {
        this.planes = planes;
    }
}
