package com.barclays.auth.controllers;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barclays.auth.domain.TokenRequest;
import com.barclays.auth.domain.TokenResponse;
import com.barclays.auth.services.JwtService;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }


    @PostMapping("/token")
    public ResponseEntity<TokenResponse> generateToken(@RequestBody @Valid TokenRequest request) {
        String email = request.getEmail();

        String token = jwtService.generateToken(
                email,
                Map.of("email", email)
        );

        return ResponseEntity.ok(new TokenResponse(token));
    }
}
