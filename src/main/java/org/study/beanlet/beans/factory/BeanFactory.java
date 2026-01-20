package org.study.beanlet.beans.factory;

import java.lang.reflect.InvocationTargetException;

public interface BeanFactory {
    Object getBean(String beanName) throws InvocationTargetException, InstantiationException, IllegalAccessException;


    Object getQualifiedBean(String beanName, String value) throws InvocationTargetException, InstantiationException, IllegalAccessException;
}
