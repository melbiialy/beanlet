package org.study.test.service;

import org.study.ioc.annotation.Autowired;
import org.study.ioc.annotation.Component;
import org.study.test.repo.Repository;
import org.study.test.repo.userRepo;

@Component
public class userService {
    @Autowired
public Repository repository;


@Autowired
    public userService() {
    }

}
