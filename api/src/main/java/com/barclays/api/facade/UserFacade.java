package com.barclays.api.facade;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.barclays.api.domain.User;
import com.barclays.api.exceptions.DuplicateResourceException;
import com.barclays.api.services.UserService;

@Service
public class UserFacade {

    private final UserService userService;

    @Autowired
    public UserFacade(UserService userService) {
        this.userService = userService;
    }

    public User create(@NotNull User user) {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        User existingUser = userService.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new DuplicateResourceException("User with email " + user.getEmail() + " already exists.");
        }

        return userService.saveUser(user);
    }
}
