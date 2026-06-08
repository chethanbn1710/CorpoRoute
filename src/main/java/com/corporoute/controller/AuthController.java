package com.corporoute.controller;

import com.corporoute.entity.User;
import com.corporoute.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestBody User user) {

        return ResponseEntity.ok(
                userService.createUser(user)
        );
    }
}