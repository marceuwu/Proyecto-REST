package com.itq.userService.business;

import java.util.List;

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

    public User getUser(int username) {
        return userDao.getUserById(username);
    }

    public boolean updateUser(int userId, User user) {
        return userDao.updateUser(userId, user);
    }

    public boolean deleteUser(int userId) {
        return userDao.deleteUser(userId);
    }

    public List<User> getAllUsers() {
		return (List<User>) userDao.getAllUsers();
	}
}
