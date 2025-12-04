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
        return userCrudRepository.findByEmail(email);
    }

    public User saveUser(@NotNull User user) {
        return userCrudRepository.save(user);
    }
}
