package org.study.test;

import org.study.ioc.annotation.Autowired;
import org.study.ioc.annotation.Component;

@Component
public class B {
public B( A a){
    System.out.println(a);
}
}
