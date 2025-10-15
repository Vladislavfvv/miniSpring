package com.myspring;

import com.myspring.annotation.Autowired;
import com.myspring.annotation.Component;
import com.myspring.annotation.Scope;
import com.myspring.configuration.BeanDefinition;
import com.myspring.configuration.InitializingBean;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MiniApplicationContext {
    private final Map<Class<?>, Object> singletonBeans = new HashMap<>();//Хранит уже созданные singleton-экземпляры
    private final Map<Class<?>, Class<?>> componentTypes = new HashMap<>(); //Хранит все классы, помеченные @Component, даже если они ещё не созданы

    private final Map<Class<?>, BeanDefinition> beanDefinition = new HashMap<>();


    public MiniApplicationContext() {
        try {
            instantiateComponents("com.myspring");
            instantiateSingletons();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing context", e);
        }
    }

    

    public <T> Object getBean(Class<T> type) {
        try {
            if (singletonBeans.containsKey(type)) {
                return singletonBeans.get(type);
            } else if (componentTypes.containsKey(type)) {
                return componentTypes.get(type);
            } else throw new RuntimeException("Bean " + type.getName() + " not found");
        } catch (RuntimeException e) {
            throw new RuntimeException("Bean " + type.getName() + " not found", e);
        }
    }


    public void instantiateComponents(String basePackage) {
        try {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            String path = basePackage.replace(".", "/");
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                File root = new File(url.toURI());
                scanDirectory(root, basePackage);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void scanDirectory(File directory, String packageName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName()); // рекурсивно заходим в подпапку
            } else if (file.getName().endsWith(".class")) {
                String className = file.getName().replace(".class", "");
                Class<?> clazz = Class.forName(packageName + "." + className);

                if (clazz.isAnnotationPresent(Component.class)) {
                    System.out.println("Component найден: " + clazz.getName());
                    Object instance = null;
                    try {
                        instance = clazz.getDeclaredConstructor().newInstance();
                    } catch (InvocationTargetException | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                    singletonBeans.put(clazz, instance);
                }
            }
        }
    }

    private void instantiateSingletons() throws Exception {
        for(Class<?> clazz : singletonBeans.keySet()){
            if(!isPrototype(clazz)){
                createBean(clazz);
            }
        }
    }

    private boolean isPrototype(Class<?> clazz) {
        Scope scope = clazz.getDeclaredAnnotation(Scope.class);
        return scope != null && "proto".equalsIgnoreCase(scope.value());
    }

    private void createBean(Class<?> clazz) throws Exception {
        if (singletonBeans.containsKey(clazz)) {
            singletonBeans.get(clazz);
            return;
        }
        Object instance = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Class<?> dependencyType = field.getType();
                Object dependency = getBean(dependencyType);
                field.setAccessible(true);
                field.set(instance, dependency);
            }
        }

        if(instance instanceof InitializingBean){
            ((InitializingBean) instance).afterPropertiesSet();
        }
        if(!isPrototype(clazz)){
            singletonBeans.put(clazz, instance);
        }
    }

    public void printBeans() {
        System.out.println("\nBeanFactory container:");
        singletonBeans.forEach((clazz, instance) -> {
            System.out.println(clazz.getName() + " : " + instance);
        });
    }
}
