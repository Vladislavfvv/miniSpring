package com.myspring;

import com.myspring.entity.Plane;
import com.myspring.service.FlyingService;


public class Main {
    public static void main(String[] args) {
        MiniApplicationContext context = new MiniApplicationContext("com.myspring");
        Plane plane = new Plane();
        FlyingService flyingService = context.getBean(FlyingService.class);
        context.getBean(Plane.class);
        flyingService.setPlanes(plane);

        context.printBeans();
    }
}
