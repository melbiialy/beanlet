package org.study.beanlet.scanner;

import org.study.beanlet.processor.BeanFactoryPostProcessor;
import org.study.beanlet.processor.BeanPostProcessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProcessorScanner {
    List<BeanFactoryPostProcessor> beanFactoryPostProcessors;
    List<BeanPostProcessor> beanPostProcessors;

    private final ClassPathScanner classPathScanner;

    public ProcessorScanner(ClassPathScanner classPathScanner) throws ClassNotFoundException {
        this.classPathScanner = classPathScanner;
        beanPostProcessors = new ArrayList<>();
        beanFactoryPostProcessors = new ArrayList<>();
        scan("");

    }

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return beanFactoryPostProcessors;
    }

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return beanPostProcessors;
    }

    public List<BeanPostProcessor> scan(String basePackage) throws ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        classPathScanner.loadClasses(basePackage, classes);


        for (Class<?> clazz : classes) {
            if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                BeanPostProcessor instance = (BeanPostProcessor) instantiate(clazz);
                beanPostProcessors.add(instance);
            }
            if (BeanFactoryPostProcessor.class.isAssignableFrom(clazz)) {
                BeanFactoryPostProcessor instance = (BeanFactoryPostProcessor) instantiate(clazz);
                beanFactoryPostProcessors.add(instance);
            }
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }

            BeanPostProcessor instance = (BeanPostProcessor) instantiate(clazz);
            beanPostProcessors.add(instance);
        }

        return beanPostProcessors;
    }

    private Object instantiate(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(); // requires a no-arg constructor
            constructor.setAccessible(true);
            return (BeanPostProcessor) constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "BeanPostProcessor " + clazz.getSimpleName() + " must have a no-arg constructor", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(
                    "Constructor of " + clazz.getSimpleName() + " threw an exception during instantiation", e.getCause());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(
                    "Failed to instantiate BeanPostProcessor: " + clazz.getSimpleName(), e);
        }
    }
}