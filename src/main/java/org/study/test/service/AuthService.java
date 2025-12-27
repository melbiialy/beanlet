package org.study.test.service;

import org.study.ioc.annotation.Autowired;
import org.study.ioc.annotation.Component;

@Component
public class AuthService {
    @Autowired
    private userService userService;
}
