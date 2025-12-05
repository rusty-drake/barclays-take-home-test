package com.barclays.api.facade;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.barclays.api.domain.User;
import com.barclays.api.exceptions.DuplicateResourceException;
import com.barclays.api.services.UserService;

@Service
@Validated
public class UserFacade {

    private final UserService userService;

    @Autowired
    public UserFacade(UserService userService) {
        this.userService = userService;
    }

    public User create(@NotNull User user, @NotNull String principalEmail) {

        if (!user.getEmail().equals(principalEmail)) {
            throw new SecurityException("Authenticated user does not match the user being created.");
        }

        User existingUser = userService.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new DuplicateResourceException("User with email " + user.getEmail() + " already exists.");
        }

        return userService.saveUser(user);
    }

    public User getUser(@NotNull Long userId, @NotNull String principalEmail) {

        User user = userService.getById(userId);

        if (!user.getEmail().equals(principalEmail)) {
            throw new SecurityException("Authenticated user does not have access to this user.");
        }

        return user;
    }   
}
