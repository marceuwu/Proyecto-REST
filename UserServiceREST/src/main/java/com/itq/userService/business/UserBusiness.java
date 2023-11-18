package com.itq.userService.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.itq.userService.dao.UserDao;
import com.itq.userService.dto.User;

@Repository
public class UserBusiness {

    @Autowired
    private UserDao userDao;

    public boolean createUser(User user) {
        return userDao.createUser(user);
    }

}
