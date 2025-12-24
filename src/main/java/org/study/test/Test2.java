package org.study.test;

import org.study.ioc.annotation.Autowired;
import org.study.ioc.annotation.Component;

@Component
public class Test2 {
    @Autowired
    public Test2(TestBean testBean) {
        System.out.println("Test2 Bean Constructor" + testBean);
    }
}
