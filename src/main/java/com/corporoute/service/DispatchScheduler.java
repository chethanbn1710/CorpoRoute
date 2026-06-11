package com.corporoute.service;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import com.corporoute.repository.RideRepository;
import com.corporoute.entity.Ride;
import com.corporoute.enums.RideStatus;

import java.util.List;

@Service
public class DispatchScheduler {

    private final RideRepository rideRepository;
    private final RideService rideService;

    public DispatchScheduler(RideRepository rideRepository, RideService rideService) {
        this.rideRepository = rideRepository;
        this.rideService = rideService;
    }

    @Scheduled(fixedRate = 5000)
    public void processDispatchRounds() {

        List<Ride> pendingRides = rideRepository.findAll().stream()
                .filter(ride -> ride.getStatus() == RideStatus.PENDING).toList();

        System.out.println("Pending rides: " + pendingRides.size());

        for (Ride ride : pendingRides) {
            
            rideService.processDispatchRound(ride);
        }
            
    }
}