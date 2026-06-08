package com.corporoute.service;

import com.corporoute.entity.Ride;
import com.corporoute.entity.Company;
import com.corporoute.repository.RideRepository;
import com.corporoute.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import com.corporoute.entity.User;
import com.corporoute.enums.Role;
import com.corporoute.enums.RideStatus;

import com.corporoute.exception.CompanyNotFoundException;
import com.corporoute.exception.CreditLimitExceededException;
import com.corporoute.exception.InvalidRideStateException;
import com.corporoute.exception.RideNotFoundException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final CompanyRepository companyRepository;
    private final UserService userService;

    public RideService(RideRepository rideRepository,
            CompanyRepository companyRepository,
            UserService userService) {

        this.rideRepository = rideRepository;
        this.companyRepository = companyRepository;
        this.userService = userService;
    }

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    public Ride getRideById(Long id) {
        return rideRepository.findById(id).orElse(null);
    }

    public Ride createRide(Ride ride, String email) {

        Company company = companyRepository.findById(
                ride.getCompany().getId())
                .orElseThrow(() -> new CompanyNotFoundException("Company not found"));

        ride.setCompany(company);

        BigDecimal newOutstanding = company.getOutstandingBalance().add(ride.getFare());

        if (newOutstanding.compareTo(company.getCreditLimit()) > 0) {
            throw new CreditLimitExceededException("Credit limit exceeded");
        }

        User employee = userService.getUserByEmail(email);

        ride.setEmployee(employee);
        ride.setDriver(null);
        ride.setStatus(RideStatus.PENDING);

        company.setOutstandingBalance(newOutstanding);
        companyRepository.save(company);

        return rideRepository.save(ride);
    }

    public Ride acceptRide(Long rideId, String driverEmail) {

        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        if (ride.getStatus() != RideStatus.PENDING) {
            throw new InvalidRideStateException( "Only pending rides can be accepted");
        }

        User driver = userService.getUserByEmail(driverEmail);

        if (driver.getRole() != Role.DRIVER) {
            throw new RuntimeException( "Only drivers can accept rides");
        }

        ride.setDriver(driver);
        ride.setStatus(RideStatus.ACCEPTED);

        return rideRepository.save(ride);
    }

    public Ride completeRide(Long rideId, String driverEmail) {

        Ride ride = rideRepository.findById(rideId)
            .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new InvalidRideStateException("Only accepted rides can be completed");
        }

        User driver = userService.getUserByEmail(driverEmail);

        if (ride.getDriver() == null || !ride.getDriver().getId().equals(driver.getId())) {
            throw new InvalidRideStateException("Only assigned driver can complete this ride");
        }

        ride.setStatus(RideStatus.COMPLETED);
        return rideRepository.save(ride);
    }

    public Ride updateRide(Long id, Ride rideDetails) {

        Ride ride = rideRepository.findById(id).orElse(null);

        if (ride != null) {
            ride.setPickupLocation(rideDetails.getPickupLocation());
            ride.setDropLocation(rideDetails.getDropLocation());
            ride.setFare(rideDetails.getFare());
            ride.setStatus(rideDetails.getStatus());

            return rideRepository.save(ride);
        }

        return null;
    }

    public List<Ride> getMyBookedRides(String email) {
        User employee = userService.getUserByEmail(email);
        return rideRepository.findByEmployee(employee);
    }

    public List<Ride> getMyAssignedRides(String email) {
        User driver = userService.getUserByEmail(email);
        return rideRepository.findByDriver(driver);
    }

    public void deleteRide(Long id) {
        rideRepository.deleteById(id);
    }
}