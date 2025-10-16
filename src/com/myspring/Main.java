package com.myspring;

import com.myspring.service.ReportService;


public class Main {
    public static void main(String[] args) {
        MiniApplicationContext context = new MiniApplicationContext("com.myspring");
        ReportService service = context.getBean(ReportService.class);
        context.printBeans();

        context.close(); // вручную вызываем завершение
    }
}
