package org.study.ioc.beans.factory;

import org.study.ioc.annotation.Autowired;
import org.study.ioc.beans.defintion.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;

public abstract class AbstractAutowireCapableBeanFactory implements BeanFactory{

    public Object createBean(String beanName, BeanDefinition beanDefinition) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> [] constructors = beanDefinition.getBeanClass().getConstructors();
        Constructor<?> constructor = resolveConstructor(constructors);
        if (constructor == null){
            throw new RuntimeException("No suitable constructor found for bean: " + beanName);
        }
        Parameter[] parameter = constructor.getParameters();
        Object[] args = new Object[parameter.length];
        for (int i = 0; i < parameter.length; i++) {
            Class<?> paramType = parameter[i].getType();
            args[i] = getBean(paramType.getCanonicalName());
        }
        constructor.setAccessible(true);
        return constructor.newInstance(args);
    }

    private Constructor<?> resolveConstructor(Constructor<?>[] constructors) {
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Autowired.class)){
                return constructor;
            }
        }
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0){
                return constructor;
            }
        }
        return null;
    }


}
