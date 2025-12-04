package com.barclays.api.services;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

import com.barclays.api.dao.UserCrudRepository;
import com.barclays.api.domain.User;

@Service
public class UserService {

    private final UserCrudRepository userCrudRepository;

    public UserService(UserCrudRepository userCrudRepository) {
        this.userCrudRepository = userCrudRepository;
    }

    public User findByEmail(@NotNull String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        return userCrudRepository.findByEmail(email);
    }

    public User saveUser(@NotNull User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return userCrudRepository.save(user);
    }
}
