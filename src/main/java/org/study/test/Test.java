package org.study.test;

import org.study.ioc.annotation.Autowired;
import org.study.ioc.annotation.Component;

@Component
public class Test implements TestInterface{
   private TestBean testBean;

    @Autowired
    public Test(TestBean testBean) {
        this.testBean = testBean;
    }
}
