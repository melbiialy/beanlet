package org.study.beanlet.beans.factory.support.beanregistry;



import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class SingletonBeanRegistry implements BeanScopeRegistry {
    private final Map<String,Object> singletonBeans;
    private final Map<String,Object> earlySingletonObjects;
    private final Map<String, Supplier<Object>> singletonFactories;

    public SingletonBeanRegistry() {

        singletonBeans = new ConcurrentHashMap<>();
        earlySingletonObjects = new ConcurrentHashMap<>();
        singletonFactories = new ConcurrentHashMap<>();
    }
    @Override
    public Object retrieveBean(String beanName, boolean allowEarlyReference){
        if (allowEarlyReference){
            Supplier<Object> singletonFactory = singletonFactories.remove(beanName);
            if (singletonFactory != null){
                Object singletonObject = singletonFactory.get();
                earlySingletonObjects.put(beanName, singletonObject);
                return singletonObject;
            }
        }
        if (singletonBeans.containsKey(beanName)){
            return singletonBeans.get(beanName);
        }
        if (earlySingletonObjects.containsKey(beanName) && allowEarlyReference){
            return earlySingletonObjects.get(beanName);
        }
        return null;
    }
    @Override
    public void registerBean(String beanName, Object bean){
        earlySingletonObjects.remove(beanName);
        singletonFactories.remove(beanName);
        singletonBeans.put(beanName,bean);
    }
    @Override
    public void registerFactory(String beanName, Supplier<Object> singletonFactory){
        singletonFactories.put(beanName,singletonFactory);
    }

    @Override
    public void clear() {
        singletonBeans.clear();
        earlySingletonObjects.clear();
        singletonFactories.clear();
    }

    @Override
    public boolean containsBean(String beanName) {
        return singletonBeans.containsKey(beanName);
    }

    @Override
    public List<String> getBeanNames() {
        return singletonBeans.keySet().stream().toList();
    }

    @Override
    public int getBeanCount() {
        return singletonBeans.size();
    }


}
