package org.study.beanlet.processor;

public interface BeanPostProcessor {
   default Object postProcessBeforeInitialization(Class<?> beanClass, String beanName){
       return null;
   }
   default boolean postProcessAfterInitialization(Object bean, String beanName){
       return false;
   }
}
