package org.study;

import org.study.beanlet.annotation.Component;
import org.study.beanlet.annotation.Scope;
import org.study.beanlet.beans.definition.BeanScope;

@Component
@Scope(BeanScope.PROTOTYPE)
public class BeanB {
    private BeanA beanA;
    public BeanB(BeanA beanA) {
        this.beanA = beanA;
    }
}
