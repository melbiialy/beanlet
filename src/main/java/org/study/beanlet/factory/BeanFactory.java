package org.study.beanlet.factory;

public interface BeanFactory  extends AutoCloseable{
    Object getBean(String beanName) throws Exception;


    String  getValue(String path);

    Object getBeanByType(Class<?> dependencyType, String value) throws Exception;
    void  registerBean(String beanName, Object bean) throws Exception;
}
