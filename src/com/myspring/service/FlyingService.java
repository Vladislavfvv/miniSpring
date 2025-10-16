package com.myspring.service;

import com.myspring.annotation.Autowired;
import com.myspring.annotation.Component;
import com.myspring.configuration.DisposableBean;
import com.myspring.configuration.InitializingBean;
import com.myspring.entity.Plane;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Component
public class FlyingService implements InitializingBean, DisposableBean {
    @Override
    public void destroy() {
        System.out.println("FlyingService очищен через DisposableBean");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Method afterPropertiesSet() in " + MethodHandles.lookup().lookupClass().getSimpleName() + " was called");
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
