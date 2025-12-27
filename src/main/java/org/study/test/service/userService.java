package org.study.test.service;

import org.study.ioc.annotation.Autowired;
import org.study.ioc.annotation.Component;
import org.study.test.repo.userRepo;

@Component
public class userService {

    private userRepo userRepo;
    public AuthService authService;
@Autowired
    public userService(userRepo userRepo, AuthService authService) {
        this.userRepo = userRepo;
        this.authService = authService;
    }

    public void test(){
        System.out.println(userRepo);
    }
}
