package com.barclays.api.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barclays.api.domain.User;
import com.barclays.api.facade.UserFacade;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserFacade userFacade;

    @Autowired
    public UserController(
        UserFacade userFacade
    ) {
        this.userFacade = userFacade;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user, Authentication authentication) {

        String principalEmail = authentication.getName();

        User createdUser = userFacade.create(user, principalEmail);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId, Authentication authentication) {

        String principalEmail = authentication.getName();

        User user = userFacade.getUser(null, principalEmail);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }
}
