package org.study.ioc.core;

public interface BeanFactory {
    Object getBean(String beanName);
    <T> T getBean(String beanName,Class<T> requiredType);
    <T> T getBean(Class<T> requiredType);
    String[] getBeanDefinitionNames();
    Class<?> getType(String beanName);
    boolean containsBean(String beanName);
    boolean isSingleton(String beanName);
    void destroyBean(String beanName);
    void registerSingleton(String beanName,Object singletonObject);

}
