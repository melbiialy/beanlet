package org.study.beanlet.registry;

import org.study.beanlet.bean.BeanDefinition;
import org.study.beanlet.bean.BeanScope;

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
            if (!beanDefinition.isLazy()){
                nonLazy.add(beanName);
            }
        }

        return nonLazy;
    }

    public void addTypeInjectionCache(String name, String canonicalName) {
        typeToNameCache.computeIfAbsent(name, k -> new HashSet<>()).add(canonicalName);
    }

    public String getTypeMatchBeanDefinition(String beanName,String qualifier) {
        Set<String> candidates = typeToNameCache.get(beanName);
        if (candidates == null) {
            throw new RuntimeException("No bean found for type: " + beanName);
        }
        if (candidates.size() == 1) {
            return candidates.iterator().next();
        }
        for (String candidate : candidates) {
            BeanDefinition candidateBeanDefinition = getBeanDefinition(candidate);
            if (candidateBeanDefinition.getBeanQualifiedName().equals(qualifier)) {
                return candidate;
            }
        }
        throw new RuntimeException("No unique bean found for type: " + beanName);
    }
}
