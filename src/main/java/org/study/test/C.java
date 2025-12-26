package org.study.test;

import org.study.ioc.annotation.Autowired;
import org.study.ioc.annotation.Component;

@Component
public class C {
    @Autowired
    public B a;
}
