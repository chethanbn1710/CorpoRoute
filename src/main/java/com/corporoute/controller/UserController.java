package com.corporoute.controller;

import com.corporoute.entity.User;
import com.corporoute.service.UserService;
import com.corporoute.dto.LocationUpdateRequest;

import jakarta.servlet.http.HttpServletRequest;
import com.corporoute.security.JwtUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id,@RequestBody User userDetails) {
        return userService.updateUser(id, userDetails);
    }

    @PutMapping("/me/online")
    public User goOnline(HttpServletRequest request) {
        String token = request.getHeader("Authorization")
                .replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);
        return userService.goOnline(email);
    }

    @PutMapping("/me/offline")
    public User goOffline(HttpServletRequest request) {
        String token = request.getHeader("Authorization")
                .replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);
        return userService.goOffline(email);
    }

    @PutMapping("/me/location")
    public User updateLocation(@RequestBody LocationUpdateRequest request,
        HttpServletRequest httpRequest) {

        String token = httpRequest
            .getHeader("Authorization")
            .replace("Bearer ", "");

        String email = jwtUtil.extractUsername(token);

        return userService.updateLocation(
            email, request.getLocation());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}