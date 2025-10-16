package com.myspring.configuration;

//Интерфейс жизненного цикла - вызывается после внедрения зависимостей
public interface InitializingBean {
    void afterPropertiesSet() throws  Exception;
}
