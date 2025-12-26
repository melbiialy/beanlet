package org.study.ioc.beans.factory.support;

import org.study.ioc.beans.defintion.BeanDefinition;
import org.study.ioc.beans.defintion.BeanScope;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BeanDefinitionRegistry {
    private final Map<String, BeanDefinition> beanDefinitionMap;
    private final Map<String, Set<String>> typeToNameCache;

    public BeanDefinitionRegistry() {
        beanDefinitionMap = new LinkedHashMap<>();
        typeToNameCache = new ConcurrentHashMap<>();
    }
    public void registerBeanDefinition(String beanName,BeanDefinition beanDefinition){
        beanDefinitionMap.put(beanName,beanDefinition);
    }
    public BeanDefinition getBeanDefinition(String beanName){
        return beanDefinitionMap.get(beanName);
    }
    public boolean containsBeanDefinition(String beanName){
        return beanDefinitionMap.containsKey(beanName);
    }

    public List<String> getBeanNames() {
        List<String> nonLazy = new ArrayList<>();
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            System.out.println(beanName);
            if (!beanDefinition.isLazy()){
                nonLazy.add(beanName);
            }
        }

        return nonLazy;
    }
}
