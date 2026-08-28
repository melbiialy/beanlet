package org.study.beanlet.context;



public interface ApplicationContext  {
    void refresh() throws Exception;
    Object getBean(String beanName) throws Exception;
}
