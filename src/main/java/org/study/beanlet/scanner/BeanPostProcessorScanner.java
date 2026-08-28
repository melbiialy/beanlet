package org.study.beanlet.scanner;

import org.study.beanlet.processor.BeanPostProcessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BeanPostProcessorScanner {

    private final ClassPathScanner classPathScanner;

    public BeanPostProcessorScanner(ClassPathScanner classPathScanner) {
        this.classPathScanner = classPathScanner;
    }

    public List<BeanPostProcessor> getBeanPostProcessors(String basePackage) throws ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        classPathScanner.loadClasses(basePackage, classes);

        List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

        for (Class<?> clazz : classes) {
            if (!BeanPostProcessor.class.isAssignableFrom(clazz)) {
                continue;
            }
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }

            BeanPostProcessor instance = instantiate(clazz);
            beanPostProcessors.add(instance);
        }

        return beanPostProcessors;
    }

    private BeanPostProcessor instantiate(Class<?> clazz) {
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