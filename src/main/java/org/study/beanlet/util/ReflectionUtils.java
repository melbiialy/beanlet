package org.study.beanlet.util;

import org.study.beanlet.annotation.*;

import org.study.beanlet.bean.BeanScope;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class ReflectionUtils {
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
    public static Class<?> loadClass(String className) throws ClassNotFoundException {
        return Class.forName(className,true,getClassLoader());
    }
    public static BeanScope getBeanScope(Class<?> clazz){
        if (clazz.isAnnotationPresent(Scope.class)){
            Scope scope = clazz.getAnnotation(Scope.class);
            return scope.value();
        }else {
            return BeanScope.SINGLETON;
        }
    }
    public static String getBeanQualifiedName(Class<?> clazz){
        if (clazz.isAnnotationPresent(Qualifier.class)){
            Qualifier qualifier = clazz.getAnnotation(Qualifier.class);
            return qualifier.value();
        }else {
            return clazz.getSimpleName();
        }
    }
    public static boolean isLazy(Class<?> clazz){
        return clazz.isAnnotationPresent(Lazy.class);
    }
    public static boolean isPrimary(Class<?> clazz){
        return clazz.isAnnotationPresent(Primary.class);
    }
    public static Method getInitMethod(Class<?> clazz){
        return getMethod(clazz,PostConstruct.class);
    }
    public static Method getDestroyMethod(Class<?> clazz) {
        return getMethod(clazz, PreDestroy.class);
    }
    private static Method getMethod(Class<?> clazz, Class<? extends Annotation> annotation) {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.isAnnotationPresent(annotation)){
                return declaredMethod;
            }
        }
        return null;
    }
    public static List<Method> getMethods(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Method> methods = new ArrayList<>();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.isAnnotationPresent(annotation)){
                methods.add(declaredMethod);
            }
        }
        return methods;
    }

    public static String[] getAllInterfaces(Class<?> clazz) {
        return clazz.getInterfaces().length == 0 ? new String[]{} :
                java.util.Arrays.stream(clazz.getInterfaces())
                        .map(Class::getSimpleName)
                        .toArray(String[]::new);
    }



}
