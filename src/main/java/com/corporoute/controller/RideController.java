package com.corporoute.controller;

import com.corporoute.entity.Ride;
import com.corporoute.security.JwtUtil;
import com.corporoute.service.RideService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rides")
public class RideController {

    private final RideService rideService;
    private final JwtUtil jwtUtil;

    public RideController(RideService rideService, JwtUtil jwtUtil) {
        this.rideService = rideService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<Ride> getAllRides() {
        return rideService.getAllRides();
    }

    @PostMapping
    public Ride createRide(
            @RequestBody Ride ride,
            HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        String token = authHeader.substring(7);

        String email = jwtUtil.extractUsername(token);

        return rideService.createRide(ride, email);
    }

    @GetMapping("/{id}")
    public Ride getRideById(@PathVariable Long id) {
        return rideService.getRideById(id);
    }

    @PutMapping("/{id}")
    public Ride updateRide(
            @PathVariable Long id,
            @RequestBody Ride ride) {

        return rideService.updateRide(id, ride);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRide(@PathVariable Long id) {
        rideService.deleteRide(id);
        return ResponseEntity.noContent().build();
    }
}