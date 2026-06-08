package com.corporoute.controller;

import com.corporoute.dto.LoginRequest;
import com.corporoute.entity.User;
import com.corporoute.security.JwtUtil;
import com.corporoute.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, AuthenticationManager 
        authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestBody User user) {

        return ResponseEntity.ok(
                userService.createUser(user)
        );
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(
        @RequestBody LoginRequest request) {

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getEmail(),request.getPassword()));

        String token = jwtUtil.generateToken(request.getEmail());

        return ResponseEntity.ok(token);
    }
}