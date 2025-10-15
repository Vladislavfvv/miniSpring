package com.myspring;

import com.myspring.entity.Plane;
import com.myspring.entity.ReportService;
import com.myspring.service.FlyingService;


public class Main {
    public static void main(String[] args) {
        MiniApplicationContext context = new MiniApplicationContext("com.myspring");
        ReportService service = context.getBean(ReportService.class);
        context.printBeans();

        context.close(); // вручную вызываем завершение
    }
}
