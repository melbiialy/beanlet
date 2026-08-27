package org.study.beanlet.registry;

import org.study.beanlet.bean.BeanScope;

import java.util.Map;

public class BeanCacheManager {
    private final Map<BeanScope, BeanScopeRegistry> registryMap;

    public BeanCacheManager(Map<BeanScope, BeanScopeRegistry> registryMap) {
        this.registryMap = registryMap;
    }
    public Object getBean(String beanName,boolean isFullyInitialized,BeanScope beanScope){
        if (!registryMap.containsKey(beanScope)) {
            return null;
        }
        return registryMap.get(beanScope).retrieveBean(beanName,isFullyInitialized);
    }

    public void registerBean(String beanName, BeanScope beanScope, Object bean) {
        if (!registryMap.containsKey(beanScope)) {
            return;
        }
        registryMap.get(beanScope).registerBean(beanName, bean);
    }

    public void registerEarlyFactoryBean(String beanName, Object bean, BeanScope beanScope) {
        if (!registryMap.containsKey(beanScope)){
            return;
        }
        registryMap.get(beanScope).registerFactory(beanName, () -> bean);
    }
}
