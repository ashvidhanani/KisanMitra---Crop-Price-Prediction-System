package com.croppredict.controller;

import com.croppredict.model.AuthRequest;
import com.croppredict.model.AuthResponse;
import com.croppredict.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {

        AuthResponse response = authService.register(
                request.getName(),
                request.getEmail(),
                request.getPassword()
        );

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {

        AuthResponse response = authService.login(
                request.getEmail(),
                request.getPassword()
        );

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(401).body(response);
    }
}