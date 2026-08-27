package org.study.beanlet.processor;

import java.lang.reflect.Constructor;

public interface SmartInstantiationAwareBeanPostProcessor extends BeanPostProcessor {
    Constructor<?> determineCandidateConstructor(Class<?> beanClass, String beanName) throws Exception;
}
