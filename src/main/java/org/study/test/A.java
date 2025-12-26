package org.study.test;

import org.study.ioc.annotation.Autowired;
import org.study.ioc.annotation.Component;

@Component
public class A {
    @Autowired
    public A(C b){
        System.out.println(b);
    }
}
