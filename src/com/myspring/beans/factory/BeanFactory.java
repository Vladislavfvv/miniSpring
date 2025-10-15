package com.myspring.beans.factory;

import com.myspring.annotation.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BeanFactory {
    private Map<Class<?>, Object> map = new HashMap<>();

    <T> Object getBean(Class<T> type) {
        return map.get(type);
    }

//    public void instantiateBean(String basePackage) {
//        try {
//            ClassLoader classLoader = ClassLoader.getSystemClassLoader();//загрузка классов
//
//            String path = basePackage.replace(".", "/"); //преобразование com.myspring в com/myspring
//            Enumeration<URL> resources = classLoader.getResources(path);//получение путей в файловой системе по которым можно искать class-файлы
//
//            while (resources.hasMoreElements()) {
//                URL url = resources.nextElement();
//                File file = new File(url.toURI());
//
//                for (File classFile : Objects.requireNonNull(file.listFiles())) {
//                    String fileName = classFile.getName();
//                    System.out.println(fileName);
//                    if (fileName.endsWith(".class")) {
//                        String className = fileName.substring(0, fileName.lastIndexOf("."));
//
//                        Class<?> classObject = Class.forName(basePackage + "." + className);
//
//                        if (classObject.isAnnotationPresent(Component.class)) {
//                            System.out.println("Component: " + classObject);
//
//                            Object newInstance = classObject.getDeclaredConstructor().newInstance();
//
//                            String beanName = className.substring(0, 1).toLowerCase() + className.substring(1);
//                            map.put(Class.forName(beanName),  newInstance);
//                        }
//                    }
//                }
//            }
//        } catch (IOException | ClassNotFoundException | URISyntaxException | InstantiationException |
//                 IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void instantiateBean(String basePackage) {
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
                    map.put(clazz, instance);
                }
            }
        }
    }

    public void printBeans() {
        System.out.println("\nBeanFactory container:");
        map.forEach((clazz, instance) -> {
            System.out.println(clazz.getName() + " : " + instance);
        });
    }



}
