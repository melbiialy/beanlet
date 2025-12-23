package org.study.ioc.beans.defintion;

import java.util.HashMap;
import java.util.Map;

public class BeanDefinitionRegistry {
    private final Map<String, BeanDefinition> beanDefinitionMap;

    public BeanDefinitionRegistry() {
        beanDefinitionMap = new HashMap<>();
    }
    public void registerBeanDefinition(String beanName,BeanDefinition beanDefinition){
        beanDefinitionMap.put(beanName,beanDefinition);
    }
    public BeanDefinition getBeanDefinition(String beanName){
        return beanDefinitionMap.get(beanName);
    }
}
