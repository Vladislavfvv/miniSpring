package com.myspring.entity;

import com.myspring.annotation.Component;
import com.myspring.annotation.Qualifier;
import com.myspring.configuration.DisposableBean;
import com.myspring.service.StorageService;

@Component
@Qualifier("dataBase")
public class DatabaseStorage implements StorageService, DisposableBean {
    @Override
    public void destroy() {
        System.out.println("DatabaseStorage очищен через DisposableBean");
    }
}
