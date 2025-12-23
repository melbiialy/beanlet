package org.study.ioc.core;

import java.util.HashMap;
import java.util.Map;

public class SingletonBeanRegistry {
    private final Map<String,Object> singletonBeans;

    public SingletonBeanRegistry() {
        singletonBeans = new HashMap<>();
    }

    public Object getSingleton(String beanName){
        return singletonBeans.get(beanName);
    }
    public void registerSingleton(String beanName,Object singletonObject){
        singletonBeans.put(beanName,singletonObject);
    }
}
