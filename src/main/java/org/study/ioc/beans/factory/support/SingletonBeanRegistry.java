package org.study.ioc.beans.factory.support;

import org.study.ioc.beans.factory.BeanFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class SingletonBeanRegistry {
    private final Map<String,Object> singletonBeans;
    private final Map<String,Object> earlySingletonObjects;
    private final Map<String, Supplier<Object>> singletonFactories;

    public SingletonBeanRegistry() {

        singletonBeans = new ConcurrentHashMap<>();
        earlySingletonObjects = new ConcurrentHashMap<>();
        singletonFactories = new ConcurrentHashMap<>();
    }

    public Object getSingleton(String beanName){
        return singletonBeans.get(beanName);
    }
    public void registerSingleton(String beanName,Object singletonObject){
        singletonBeans.put(beanName,singletonObject);
    }
    public boolean containsSingleton(String beanName){
        return singletonBeans.containsKey(beanName);
    }


    public boolean containsEarlyBean(String beanName) {
        return earlySingletonObjects.containsKey(beanName);
    }

    public Object getEarlyBean(String beanName) {
        return earlySingletonObjects.get(beanName);
    }

    public boolean containsFactoryBean(String beanName) {
        return singletonFactories.containsKey(beanName);
    }

    public Object getBeanFromFactory(String beanName) {
        Object bean = singletonFactories.get(beanName).get();
        singletonFactories.remove(beanName);
        earlySingletonObjects.put(beanName, bean);
        return bean;
    }

    public void addFactoryBean(String beanName, Object o) {
        singletonFactories.put(beanName, () -> o);
    }
}
