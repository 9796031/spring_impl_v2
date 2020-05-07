package com.home.service.impl;

import com.home.framework.stereotype.LQDService;
import com.home.pojo.UserEntity;
import com.home.service.UserService;

import java.util.Date;

@LQDService
public class UserServiceImpl implements UserService {
    @Override
    public UserEntity getById(String uid) {
        return new UserEntity("a", new Date());
    }


}
