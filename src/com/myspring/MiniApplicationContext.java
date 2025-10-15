package com.myspring;

import com.myspring.annotation.*;
import com.myspring.beans.factory.BeanDefinition;
import com.myspring.configuration.DisposableBean;
import com.myspring.configuration.InitializingBean;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * класс описывающий логику работы по созданию бина
 */
public class MiniApplicationContext {
    //контейнер бинов
    private final Map<Class<?>, BeanDefinition> beanFactory = new HashMap<>();

    public MiniApplicationContext(String location) {
        try {
            scanPackage(location);//сканирование рекурсивно
            instantiateSingletons();//singleton-бины создаются сразу при запуске контекста
        } catch (Exception e) {
            throw new RuntimeException("Error initializing context", e);
        }
    }

    //сканирование папок и файлов рекурсивно
    private void scanPackage(String basePackage) throws ClassNotFoundException, IOException, URISyntaxException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();//отвечает за загрузку классов
        String path = basePackage.replace('.', '/');//т.к. директории отмечены точкой то нужно их поменять чтобы били как и файлы т.е. //преобразование com.myspring в com/myspring
        Enumeration<URL> resources = classLoader.getResources(path);//получение путей в файловой системе по которым можно искать class-файлы
        //URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        if (resources == null) throw new RuntimeException("Пакет не найден: " + basePackage);

        while (resources.hasMoreElements()) {//обходим по папкам
            URL url = resources.nextElement();  //наподобие итератора
            File files = new File(url.toURI());
            // File directory = new File(resources.getFile());
            for (File file : Objects.requireNonNull(files.listFiles())) {
                if (file.isDirectory()) {
                    scanPackage(basePackage + "." + file.getName()); // рекурсивно идём в подпапки
                } else if (file.getName().endsWith(".class")) {
                    String className = basePackage + "." + file.getName().replace(".class", "");//получение названия файла
                    Class<?> clazz = Class.forName(className);//получение обьекта класса
                    if (clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Service.class) || clazz.isAnnotationPresent(Repository.class)) {
                        boolean isPrototype = clazz.isAnnotationPresent(Scope.class)
                                && "prototype".equals(clazz.getAnnotation(Scope.class).value());//определяем, является ли класс прототипом, то есть должен ли контейнер создавать новый экземпляр каждый раз, когда вызывается getBean
                        String qualifier = clazz.isAnnotationPresent(Qualifier.class)
                                ? clazz.getAnnotation(Qualifier.class).value()
                                : clazz.getSimpleName(); // fallback
                        beanFactory.put(clazz, new BeanDefinition(clazz, isPrototype, qualifier));//если false то бин создаётся один раз и сохраняется; если true то бин создаётся каждый раз заново.
                    }
                }
            }
        }
    }

    public <T> T getBean(Class<T> clazz) {
        try {
            for (BeanDefinition beanDefinition : beanFactory.values()) {
                if (clazz.isAssignableFrom(beanDefinition.getType())) {//проверка совместимости типов -> Можно ли использовать бин beanDefinition.getType() как реализацию типа clazz
                    // Если экземпляр уже создан, вернуть его
                    if (!beanDefinition.isPrototype() && beanDefinition.getInstance() != null) {
                        return clazz.cast(beanDefinition.getInstance());
                    }
                    // Если нет — создать, сохранить и вернуть
                    T instance = clazz.cast(createBean(beanDefinition.getType()));
                    if (!beanDefinition.isPrototype()) {
                        beanDefinition.setInstance(instance);
                    }
                    return instance;
                }
            }
            throw new RuntimeException("Бин не найден: " + clazz.getName());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка получения бина: " + clazz, e);
        }
    }


    private void instantiateSingletons() throws Exception {
        for (BeanDefinition bd : beanFactory.values()) {//Перебираем все зарегистрированные BeanDefinition в beanFactory
            if (bd.getInstance() == null && !bd.isPrototype()) {//если бин ещё не создан
                Object instance = createBean(bd.getType());//создать экземпляр бина
                bd.setInstance(instance);//сохранение бина
            }
        }
    }

    //Прототипы не создаются заранее — они создаются каждый раз при getBean()
    private boolean isPrototype(Class<?> clazz) {
        Scope scope = clazz.getDeclaredAnnotation(Scope.class);
        return scope != null && "proto".equalsIgnoreCase(scope.value());
    }

    private Object findBeanByTypeAndQualifier(Class<?> type, String qualifier) throws Exception {
        for (BeanDefinition bd : beanFactory.values()) {
            boolean typeMatches = type.isAssignableFrom(bd.getType());
            boolean qualifierMatches = qualifier == null || qualifier.equals(bd.getQualifier());

            if (typeMatches && qualifierMatches) {
                if (!bd.isPrototype() && bd.getInstance() != null) {
                    return bd.getInstance();
                }
                Object instance = createBean(bd.getType());
                if (!bd.isPrototype()) {
                    bd.setInstance(instance);
                }
                return instance;
            }
        }
        throw new RuntimeException("Не найден бин c типом " + type + " с qualifier '" + qualifier + "'");
    }


    private Object createBean(Class<?> clazz) throws Exception {
        BeanDefinition entity = beanFactory.get(clazz);

        if (entity == null) {
            throw new RuntimeException("Bean " + clazz.getName() + " not found");
        }
        Object instance = clazz.getDeclaredConstructor().newInstance();//получаем безаргументный конструктор класса, создаём новый объект через рефлексию

        for (Field field : clazz.getDeclaredFields()) {//перебираем все поля класса
            if (field.isAnnotationPresent(Autowired.class)) { //если поле помечено @Autowired
                String someQualifier = null;
                if (field.isAnnotationPresent(Qualifier.class)) {
                    someQualifier = field.getAnnotation(Qualifier.class).value();
                }

                Object dependency = findBeanByTypeAndQualifier(field.getType(), someQualifier);
               // Object dependency = getBean(field.getType()); //вызываем getBean() — это рекурсивно создаёт или возвращает нужный бин
                field.setAccessible(true); //делаем поле доступным, даже если оно приватное
                field.set(instance, dependency);//вставляем зависимость в поле
            }
        }

        if (instance instanceof InitializingBean) {//Если класс реализует интерфейс InitializingBean, вызывается метод afterPropertiesSet()
            ((InitializingBean) instance).afterPropertiesSet();
        }

        if (!entity.isPrototype()) {
            entity.setInstance(instance); //Если бин не помечен как @Scope("prototype"), сохраняем его как бин
        }
        return instance;
    }

    public void printBeans() {
        System.out.println("\nBeanFactory container:");
        beanFactory.forEach((clazz, instance) -> {
            System.out.println(clazz.getName() + " : " + instance.getInstance());
        });
    }

    public void close() {
        for (BeanDefinition bd : beanFactory.values()) {
            Object instance = bd.getInstance();
            if (instance == null || bd.isPrototype()) continue;

            // Вызов destroy() из DisposableBean
            if (instance instanceof DisposableBean) {
                ((DisposableBean) instance).destroy();
            }

            // Вызов метода с @PreDestroy
            for (Method method : instance.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(PreDestroy.class)) {
                    if (method.getParameterCount() != 0) {
                        throw new RuntimeException("@PreDestroy метод должен быть без аргументов: " + method);
                    }
                    method.setAccessible(true);
                    try {
                        method.invoke(instance);
                    } catch (Exception e) {
                        throw new RuntimeException("Ошибка при вызове @PreDestroy: " + method, e);
                    }
                }
            }
        }
    }
}
