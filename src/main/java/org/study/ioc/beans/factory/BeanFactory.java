package org.study.ioc.beans.factory;

import java.lang.reflect.InvocationTargetException;

public interface BeanFactory {
    Object getBean(String beanName) throws InvocationTargetException, InstantiationException, IllegalAccessException;


}
