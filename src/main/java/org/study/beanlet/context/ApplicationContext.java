package org.study.beanlet.context;

import org.study.beanlet.factory.BeanFactory;

public interface ApplicationContext extends BeanFactory {
    void refresh() throws ClassNotFoundException;
}
