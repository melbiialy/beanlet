package org.study.beanlet.core.util;

import org.study.beanlet.annotation.*;
import org.study.beanlet.beans.definition.BeanDefinition;
import org.study.beanlet.beans.definition.BeanScope;
import org.study.beanlet.beans.definition.DependencyDescriptor;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
    public static BeanDefinition extractBeanDefinition(Class<?> clazz){
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClass(clazz);
        if (clazz.isAnnotationPresent(Qualifier.class)){
            Qualifier qualifier = clazz.getAnnotation(Qualifier.class);
            beanDefinition.setBeanQualifiedName(qualifier.value());
        }else {
            beanDefinition.setBeanQualifiedName(clazz.getSimpleName());
        }
        if (clazz.isAnnotationPresent(Scope.class)){
            Scope scope = clazz.getAnnotation(Scope.class);
            beanDefinition.setBeanScope(scope.value());
        }else {
            beanDefinition.setBeanScope(BeanScope.SINGLETON);
        }
        beanDefinition.setLazy(clazz.isAnnotationPresent(Lazy.class));
        beanDefinition.setPrimary(clazz.isAnnotationPresent(Primary.class));
        Method initMethod = getMethod(clazz,PostConstruct.class);
        beanDefinition.setInitMethod(initMethod);
        Method destroyMethod = getMethod(clazz, Destroy.class);
        beanDefinition.setDestroyMethod(destroyMethod);
        beanDefinition.setInitialized(false);
        return beanDefinition;
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
}
