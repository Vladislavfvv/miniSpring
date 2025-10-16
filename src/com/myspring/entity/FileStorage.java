package com.myspring.entity;

import com.myspring.annotation.Component;
import com.myspring.annotation.Qualifier;
import com.myspring.configuration.DisposableBean;
import com.myspring.service.StorageService;

@Component
@Qualifier("file")
public class FileStorage implements StorageService, DisposableBean {
    @Override
    public void destroy() {
        System.out.println("FileStorage очищен через DisposableBean");
    }
}
