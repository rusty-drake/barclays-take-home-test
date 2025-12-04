package com.barclays.auth.controllers;

import com.barclays.auth.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Map;

import com.barclays.auth.domain.TokenRequest;;
import com.barclays.auth.domain.TokenResponse;

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
