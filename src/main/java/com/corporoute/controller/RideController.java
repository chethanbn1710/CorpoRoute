package com.corporoute.controller;

import com.corporoute.dto.DispatchCandidate;
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
    public Ride createRide(@RequestBody Ride ride,HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        return rideService.createRide(ride, email);
    }

    @GetMapping("/{id}")
    public Ride getRideById(@PathVariable Long id) {
        return rideService.getRideById(id);
    }

    @GetMapping("/{id}/nearest-drivers")
    public List<DispatchCandidate> getNearestDrivers(
            @PathVariable Long id,
            @RequestParam(defaultValue = "3") int limit) {

        return rideService.findNearestDrivers(id, limit);
    }

    @GetMapping("/{id}/eta")
    public Long getETA(@PathVariable Long id) {
        return rideService.calculateETA(id);
    }

    @GetMapping("/my-bookings")
    public List<Ride> myBookings(HttpServletRequest request) {

        String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtil.extractUsername(token);
        return rideService.getMyBookedRides(email);
    }

    @GetMapping("/my-assignments")
    public List<Ride> myAssignments(HttpServletRequest request) {

        String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtil.extractUsername(token);
        return rideService.getMyAssignedRides(email);
    }

    @PutMapping("/{id}")
    public Ride updateRide(
            @PathVariable Long id,
            @RequestBody Ride ride) {

        return rideService.updateRide(id, ride);
    }

    @PutMapping("/{id}/cancel")
    public Ride cancelRide(@PathVariable Long id,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization")
                .replace("Bearer ", "");

        String email = jwtUtil.extractUsername(token);
        return rideService.cancelRide(id, email);
    }

    @GetMapping("/{id}/dispatch-round")
    public List<DispatchCandidate> getDispatchRound(
            @PathVariable Long id,
            @RequestParam int round) {

        return rideService.getDispatchRound(id);
    }

    @GetMapping("/{id}/dispatch-candidates")
    public List<DispatchCandidate> getDispatchCandidates(
            @PathVariable Long id) {

        return rideService.getDispatchRound(id);
    }

    @PutMapping("/{id}/accept")
    public Ride acceptRide(@PathVariable Long id,HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        return rideService.acceptRide(id, email);
    }

    @PutMapping("/{id}/complete")
    public Ride completeRide(@PathVariable Long id,HttpServletRequest request) {

        String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtil.extractUsername(token);
        return rideService.completeRide(id, email);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRide(@PathVariable Long id) {
        rideService.deleteRide(id);
        return ResponseEntity.noContent().build();
    }
}