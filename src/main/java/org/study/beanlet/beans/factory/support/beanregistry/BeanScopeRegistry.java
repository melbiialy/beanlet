package org.study.beanlet.beans.factory.support.beanregistry;

import java.util.List;
import java.util.function.Supplier;

public interface BeanScopeRegistry {
    Object retrieveBean(String beanName, boolean allowEarlyReference);
    void registerBean(String beanName, Object bean);
    void registerFactory(String beanName, Supplier<Object> singletonFactory);
    void clear();
    boolean containsBean(String beanName);
    List<String> getBeanNames();
    int getBeanCount();

}
