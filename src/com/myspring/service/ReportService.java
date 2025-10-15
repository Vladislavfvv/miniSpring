package com.myspring.service;

import com.myspring.annotation.Autowired;
import com.myspring.annotation.Component;
import com.myspring.annotation.Qualifier;

@Component
public class ReportService {
    @Autowired
    @Qualifier("dataBase")
    private StorageService storageService;
}
