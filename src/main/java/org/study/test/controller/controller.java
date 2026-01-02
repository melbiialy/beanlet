package org.study.test.controller;

import org.study.ioc.annotation.Autowired;
import org.study.ioc.annotation.Component;
import org.study.test.service.userService;

@Component
public class controller {
    @Autowired
    private userService userService;
    public void test(){
    }
}
