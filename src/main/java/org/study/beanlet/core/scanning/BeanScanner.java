package org.study.beanlet.core.scanning;

import org.study.beanlet.beans.factory.support.BeanDefinitionRegistry;

public interface BeanScanner {
    void scan(BeanDefinitionRegistry registry) throws Exception;
}

