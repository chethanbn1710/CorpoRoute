package com.corporoute.service;

import com.corporoute.entity.Ride;
import com.corporoute.entity.Company;
import com.corporoute.repository.CompanyRepository;
import com.corporoute.repository.RideRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final CompanyRepository companyRepository;

    public RideService(RideRepository rideRepository,CompanyRepository companyRepository) {
        this.rideRepository = rideRepository;
        this.companyRepository = companyRepository;
    }

    public Ride createRide(Ride ride) {

        Company company = companyRepository.findById(ride.getCompany().getId())
                        .orElseThrow(() -> new RuntimeException("Company not found"));
        ride.setCompany(company);

        BigDecimal newOutstanding = company.getOutstandingBalance().add(ride.getFare());

        if (newOutstanding.compareTo(company.getCreditLimit()) > 0) {
            throw new RuntimeException("Credit limit exceeded");
        }

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
            ride.setEmployeeName(rideDetails.getEmployeeName());
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