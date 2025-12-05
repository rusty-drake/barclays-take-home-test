package com.barclays.api.services;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.barclays.api.dao.UserCrudRepository;
import com.barclays.api.domain.User;
import com.barclays.api.exceptions.ResourceNotFoundException;

@Service
@Validated
public class UserService {

    private final UserCrudRepository userCrudRepository;

    public UserService(UserCrudRepository userCrudRepository) {
        this.userCrudRepository = userCrudRepository;
    }

    public User findByEmail(@NotNull String email) {
        return userCrudRepository.findByEmail(email);
    }

    public User saveUser(@NotNull User user) {
        return userCrudRepository.save(user);
    }

    public User getById(@NotNull Long userId) {

        User user = userCrudRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("User with ID " + userId + " not found.");
        }
        return user;
    }
}
