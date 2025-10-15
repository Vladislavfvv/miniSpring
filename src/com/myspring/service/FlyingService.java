package com.myspring.service;

import com.myspring.annotation.Autowired;
import com.myspring.annotation.Component;
import com.myspring.entity.Plane;

import java.util.List;

@Component
public class FlyingService {
    @Autowired
    private List<Plane> planes;

    public List<Plane> getPlanes() {
        return planes;
    }

    public void setPlanes(List<Plane> planes) {
        this.planes = planes;
    }
}
