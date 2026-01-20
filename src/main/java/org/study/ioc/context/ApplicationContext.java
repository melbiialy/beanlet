package org.study.ioc.context;

import org.study.ioc.beans.factory.BeanFactory;

public interface ApplicationContext extends BeanFactory {
    void refresh() throws ClassNotFoundException;
}
