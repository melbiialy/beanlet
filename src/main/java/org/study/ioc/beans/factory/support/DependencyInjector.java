package org.study.ioc.beans.factory.support;

import org.study.ioc.annotation.Autowired;
import org.study.ioc.beans.defintion.BeanDefinition;
import org.study.ioc.beans.factory.DefaultBeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class DependencyInjector {


    public void injectDependencies(Object bean, BeanDefinition beanDefinition, DefaultBeanFactory defaultBeanFactory) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Field[] fields = beanClass.getDeclaredFields();
        boolean completed = true;

        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)){
                    Object injectedBean = defaultBeanFactory.getBean(field.getType().getCanonicalName());
                    field.setAccessible(true);
                    field.set(bean, injectedBean);
            }
        }

    }
}
