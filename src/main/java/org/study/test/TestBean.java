package org.study.test;

import org.study.ioc.annotation.Autowired;
import org.study.ioc.annotation.Component;
import org.study.ioc.annotation.Scope;
import org.study.ioc.beans.defintion.BeanScope;

@Component
public class TestBean {
    @Autowired
    public Test test;
}
