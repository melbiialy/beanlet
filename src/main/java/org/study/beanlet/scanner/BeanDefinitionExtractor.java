package org.study.beanlet.scanner;

import org.study.beanlet.bean.BeanDefinition;

import java.util.List;

public interface BeanDefinitionExtractor {
    List<BeanDefinition> extract(Class<?> clazz);
    boolean support(Class<?> clazz);
}
