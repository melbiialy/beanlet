package org.study.test.repo;

import org.study.ioc.annotation.Component;

@Component
public class userRepo implements Repository {
    public void test(){
        System.out.println("userRepo");
    }
}
