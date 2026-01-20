package org.study.beanlet.beans.factory.support.scope;



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
    public Object getSingleton(String beanName,boolean allowEarlyReference){
        if (singletonBeans.containsKey(beanName)){
            return singletonBeans.get(beanName);
        }
        if (earlySingletonObjects.containsKey(beanName) && allowEarlyReference){
            return earlySingletonObjects.get(beanName);
        }
        if (singletonFactories.containsKey(beanName) && allowEarlyReference){
            Object singletonObject = singletonFactories.get(beanName).get();
            earlySingletonObjects.put(beanName,singletonObject);
            singletonFactories.remove(beanName);
            return singletonObject;
        }
        return null;
    }
    public void registerSingleton(String beanName,Object bean){
        earlySingletonObjects.remove(beanName);
        singletonFactories.remove(beanName);
        singletonBeans.put(beanName,bean);
    }
    public void registerSingletonFactory(String beanName,Supplier<Object> singletonFactory){
        singletonFactories.put(beanName,singletonFactory);
    }
    public void addEarlySingletonObject(String beanName,Object singletonObject){
        earlySingletonObjects.put(beanName,singletonObject);
        singletonFactories.remove(beanName);
    }

}
