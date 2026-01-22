package org.study.beanlet.core.scanning;

import org.study.beanlet.beans.definition.BeanDefinition;

import java.util.List;

public interface Extractor {
    List<BeanDefinition> extract(Class<?> clazz);
    boolean support(Class<?> clazz);
}
