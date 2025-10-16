package com.myspring.service;

import com.myspring.annotation.Autowired;
import com.myspring.annotation.Component;
import com.myspring.annotation.Qualifier;
import com.myspring.configuration.DisposableBean;
import com.myspring.configuration.InitializingBean;

import java.lang.invoke.MethodHandles;

@Component
public class ReportService implements InitializingBean, DisposableBean {
    @Override
    public void destroy() {
        System.out.println("ReportService очищен через DisposableBean");
    }

    @Autowired
    @Qualifier("dataBase")
    private StorageService storageService;


    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Method afterPropertiesSet() in " + MethodHandles.lookup().lookupClass().getSimpleName() + " was called");
    }
}
