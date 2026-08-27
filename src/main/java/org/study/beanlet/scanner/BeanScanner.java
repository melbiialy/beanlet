package org.study.beanlet.scanner;

import org.study.beanlet.registry.BeanDefinitionRegistry;

public interface BeanScanner {
    void scan(BeanDefinitionRegistry registry) throws Exception;
}
