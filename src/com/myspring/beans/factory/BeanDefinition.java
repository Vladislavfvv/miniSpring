package com.myspring.beans.factory;

/**
 * объект для описания бина
 * instance - отдельное поле, которое устанавливается через setInstance(...) после создания бина — т.к это подход, аналогичный Spring
 *
 */
public class BeanDefinition {
    private final Class<?> type;//тип бина
    private Object instance;//для хранения бина
    private final boolean isPrototype;//маркер прототайп или синглетон
    private final String qualifier;

    //в конструкторе бин на создаем(т.е. private Object instance;), чтобы не нарушать инверсию управления (IoC),
    // его заполнение будет позже через сеттер setInstance и оно зависит от области действия (singleton или prototype),
    public BeanDefinition(Class<?> type, boolean isPrototype, String qualifier) {
        this.type = type;
        this.isPrototype = isPrototype;
        this.qualifier = qualifier;
    }

    public String getQualifier() {
        return qualifier;
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

    public void setInstance(Object instance) {
        this.instance = instance;
    }
}
