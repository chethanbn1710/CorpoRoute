package com.corporoute.service;

import com.corporoute.entity.Ride;
import com.corporoute.exception.CompanyNotFoundException;
import com.corporoute.exception.CreditLimitExceededException;
import com.corporoute.entity.Company;
import com.corporoute.repository.CompanyRepository;
import com.corporoute.repository.RideRepository;
import org.springframework.stereotype.Service;

import com.corporoute.entity.User;
import com.corporoute.enums.RideStatus;

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

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    public Ride getRideById(Long id) {
        return rideRepository.findById(id).orElse(null);
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

    public void deleteRide(Long id) {
        rideRepository.deleteById(id);
    }
}