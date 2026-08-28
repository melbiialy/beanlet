package org.study.beanlet.registry;

import org.study.beanlet.bean.BeanDefinition;
import org.study.beanlet.bean.BeanScope;
import org.study.beanlet.exception.BeanNotFoundException;
import org.study.beanlet.exception.NoUniqueBeanDefinitionException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BeanDefinitionRegistry {
    private final Map<String, BeanDefinition> beanDefinitionMap;
    private final Map<Class<?>, List<String>> typeToNameCache;

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



    public String getTypeMatchBeanDefinition(Class<?> dependencyType,String qualifierValue) {
        List<String> candidates = typeToNameCache.get(dependencyType);
        if (candidates == null) {

             candidates = new ArrayList<>();
            for (String beanName : this.getBeanNames()) {
                BeanDefinition definition = this.getBeanDefinition(beanName);
                if (dependencyType.isAssignableFrom(definition.getBeanClass())) {
                    candidates.add(beanName);
                }
            }
        }

        typeToNameCache.put(dependencyType,candidates);
        if (candidates.isEmpty()) {
            throw new BeanNotFoundException(
                    "No bean found matching type: " + dependencyType.getSimpleName());
        }


        if (candidates.size() > 1) {
            List<String> qualifiedCandidates = new ArrayList<>();
            String  primary = null;
            for (String candidateName : candidates) {
                BeanDefinition definition = this.getBeanDefinition(candidateName);
                if (definition.isPrimary()){
                    if (primary == null) {
                        primary = candidateName;
                    }else {
                        throw new NoUniqueBeanDefinitionException(
                                "Expected a single bean matching type " + dependencyType.getSimpleName() +
                                        " but found " + candidates.size() + " candidates: " + candidates +
                                        " — consider using @Qualifier to disambiguate");
                    }
                }
                if (qualifierValue != null && (qualifierValue.equals(definition.getBeanQualifiedName()) || qualifierValue.equals(candidateName))) {
                    qualifiedCandidates.add(candidateName);
                }
            }
            if (qualifierValue == null) {
                if (primary != null) {
                    return primary;
                }
                throw new NoUniqueBeanDefinitionException(
                        "Expected a single bean matching type " + dependencyType.getSimpleName() +
                                " but found " + candidates.size() + " candidates: " + candidates +
                                " — consider using @Qualifier to disambiguate");
            }

            if (qualifiedCandidates.isEmpty()) {
                throw new BeanNotFoundException(
                        "No bean matching type " + dependencyType.getSimpleName() +
                                " with qualifier '" + qualifierValue + "' found among candidates: " + candidates);
            }
            if (qualifiedCandidates.size() > 1) {
                throw new NoUniqueBeanDefinitionException(
                        "Multiple beans matching type " + dependencyType.getSimpleName() +
                                " with qualifier '" + qualifierValue + "': " + qualifiedCandidates);
            }

            candidates = qualifiedCandidates;
        }

        return candidates.getFirst();
    }
}
