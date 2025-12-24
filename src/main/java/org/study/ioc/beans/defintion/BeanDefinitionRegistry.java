package org.study.ioc.beans.defintion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BeanDefinitionRegistry {
    private final Map<String, BeanDefinition> beanDefinitionMap;
    private final Map<String, Set<String>> typeToNameCache;

    public BeanDefinitionRegistry() {
        beanDefinitionMap = new ConcurrentHashMap<>();
        typeToNameCache = new ConcurrentHashMap<>();
    }
    public void registerBeanDefinition(String beanName,BeanDefinition beanDefinition){
        beanDefinitionMap.put(beanName,beanDefinition);
    }
    public BeanDefinition getBeanDefinition(String beanName){
        return beanDefinitionMap.get(beanName);
    }
}
