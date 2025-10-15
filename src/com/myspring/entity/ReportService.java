package com.myspring.entity;

import com.myspring.annotation.Autowired;
import com.myspring.annotation.Component;
import com.myspring.annotation.Qualifier;
import com.myspring.configuration.DisposableBean;
import com.myspring.configuration.InitializingBean;
import com.myspring.service.StorageService;

@Component
public class ReportService {
    @Autowired
    @Qualifier("dataBase")
    private StorageService storageService;
}
