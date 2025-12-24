package org.study.ioc.beans.factory.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SingletonBeanRegistry {
    private final Map<String,Object> singletonBeans;

    public SingletonBeanRegistry() {
        singletonBeans = new ConcurrentHashMap<>();
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

}
