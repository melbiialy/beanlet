package org.study.beanlet.beans.factory.support;

import org.study.beanlet.annotation.Autowired;
import org.study.beanlet.beans.definition.BeanDefinition;
import org.study.beanlet.beans.factory.DefaultBeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DependencyInjector {
    public void fieldsInjection(Object bean, BeanDefinition beanDefinition, DefaultBeanFactory defaultBeanFactory) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)){
                if (field.getType().isInterface()) {
                    initializeDependency(bean, defaultBeanFactory, field);
                    continue;
                }
                Object injectedBean = defaultBeanFactory.getBean(field.getType().getName());
                field.setAccessible(true);
                field.set(bean, injectedBean);
            }

        }
    }
    public void methodsInjection(Object bean, BeanDefinition beanDefinition, DefaultBeanFactory defaultBeanFactory) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Method [] methods = beanClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Autowired.class)){
                Class<?>[] parameterTypes = method.getParameterTypes();
                Object[] args = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (parameterTypes[i].isInterface()){
                        String qualifier = null;
                        if (method.getParameters()[i].isAnnotationPresent(org.study.beanlet.annotation.Qualifier.class)){
                            qualifier = method.getParameters()[i].getAnnotation(org.study.beanlet.annotation.Qualifier.class).value();
                        }
                        args[i] = defaultBeanFactory.getQualifiedBean(parameterTypes[i].getName(), qualifier);
                        continue;
                    }
                    args[i] = defaultBeanFactory.getBean(parameterTypes[i].getName());
                }
                method.setAccessible(true);
                method.invoke(bean, args);

            }
        }

    }

    private static void initializeDependency(Object bean, DefaultBeanFactory defaultBeanFactory, Field field) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        String qualifier = null;
        if (field.isAnnotationPresent(org.study.beanlet.annotation.Qualifier.class)){
            qualifier = field.getAnnotation(org.study.beanlet.annotation.Qualifier.class).value();
        }
        Object injectedBean = defaultBeanFactory.getQualifiedBean(field.getType().getCanonicalName(), qualifier);
        field.setAccessible(true);
        field.set(bean, injectedBean);
        return;
    }
}
