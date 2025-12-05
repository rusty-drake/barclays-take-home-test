package com.barclays.api.services;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.barclays.api.dao.UserDao;
import com.barclays.api.domain.User;
import com.barclays.api.exceptions.ResourceNotFoundException;

@Service
@Validated
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User findByEmail(@NotNull String email) {
        return userDao.findByEmail(email);
    }

    public User saveUser(@NotNull User user) {
        return userDao.save(user);
    }

    public User getById(@NotNull Long userId) {

        User user = userDao.findById(userId).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("User with ID " + userId + " not found.");
        }
        return user;
    }
}
