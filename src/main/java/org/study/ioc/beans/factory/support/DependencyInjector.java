package org.study.ioc.beans.factory.support;

import org.study.ioc.annotation.Autowired;
import org.study.ioc.beans.defintion.BeanDefinition;
import org.study.ioc.beans.factory.DefaultBeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DependencyInjector {


    public void injectDependencies(Object bean, BeanDefinition beanDefinition, DefaultBeanFactory defaultBeanFactory) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)){
                    Object injectedBean = defaultBeanFactory.getBean(field.getType().getCanonicalName());
                    field.setAccessible(true);
                    field.set(bean, injectedBean);
            }
        }
        Method [] methods = beanClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Autowired.class)){
                Class<?>[] parameterTypes = method.getParameterTypes();
                Object[] args = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    args[i] = defaultBeanFactory.getBean(parameterTypes[i].getCanonicalName());
                }
                method.setAccessible(true);
                method.invoke(bean, args);

            }
        }

    }
}
