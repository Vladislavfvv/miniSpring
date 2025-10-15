package com.myspring.configuration;

public class BeanDefinition {
    private final Class<?> type;
    private Object instance;
    private final boolean isPrototype;

    public BeanDefinition(Class<?> type, boolean isPrototype) {
        this.type = type;
        this.isPrototype = isPrototype;
    }

    public Class<?> getType() {
        return type;
    }

    public Object getInstance() {
        return instance;
    }

   public boolean isPrototype() {
        return isPrototype;
   }
}
