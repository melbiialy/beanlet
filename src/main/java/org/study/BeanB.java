package org.study;

import org.study.beanlet.annotation.Destroy;
import org.study.beanlet.annotation.PostConstruct;

public class BeanB {
    @PostConstruct
    public void init(){
        System.out.println("init");
    }
    @Destroy
    public void destroy(){
        System.out.println("destroy");
    }
}
