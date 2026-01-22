package org.study;

import org.study.beanlet.annotation.Component;

@Component
public class BeanA {
    private BeanB beanB;
    public BeanA(BeanB beanB) {
        this.beanB = beanB;
    }
}
