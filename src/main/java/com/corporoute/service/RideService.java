package com.corporoute.service;

import com.corporoute.entity.Ride;
import com.corporoute.entity.Company;
import com.corporoute.repository.RideRepository;
import com.corporoute.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import com.corporoute.entity.User;
import com.corporoute.enums.Role;
import com.corporoute.enums.RideStatus;
import com.corporoute.repository.UserRepository;

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
    private final UserRepository userRepository;

    public RideService(RideRepository rideRepository, CompanyRepository companyRepository,
            UserService userService, UserRepository userRepository) {

        this.rideRepository = rideRepository;
        this.companyRepository = companyRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    public Ride getRideById(Long id) {
        return rideRepository.findById(id)
            .orElseThrow(() -> new RideNotFoundException("Ride not found"));
    }

    public Ride createRide(Ride ride, String email) {

        User employee = userService.getUserByEmail(email);
        Company company = employee.getCompany();

        if (company == null) {throw new CompanyNotFoundException(
                    "Employee is not associated with any company");
        }

        BigDecimal projectedExposure = company.getOutstandingBalance()
                .add(company.getReservedCredit()).add(ride.getFare());

        if (projectedExposure.compareTo(company.getCreditLimit()) > 0) {
            throw new CreditLimitExceededException("Credit limit exceeded");
        }

        company.setReservedCredit(company.getReservedCredit().add(ride.getFare()));

        companyRepository.save(company);

        ride.setEmployee(employee);
        ride.setCompany(company);
        ride.setDriver(null);
        ride.setStatus(RideStatus.PENDING);

        return rideRepository.save(ride);
    }


    public Ride cancelRide(Long rideId, String employeeEmail) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        User employee = userService.getUserByEmail(employeeEmail);

        if (!ride.getEmployee().getId().equals(employee.getId())) {
            throw new InvalidRideStateException("You can only cancel your own rides");
        }

        if (ride.getStatus() != RideStatus.PENDING) {
            throw new InvalidRideStateException("Only pending rides can be cancelled");
        }

        Company company = ride.getCompany();
        company.setReservedCredit(company.getReservedCredit().subtract(ride.getFare()));

        companyRepository.save(company);
        ride.setStatus(RideStatus.CANCELLED);

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
            throw new InvalidRideStateException( "Only drivers can accept rides");
        }

        if (!driver.getAvailable()) {
            throw new InvalidRideStateException("Driver is offline");
        }

        ride.setDriver(driver);
        ride.setStatus(RideStatus.ACCEPTED);

        driver.setAvailable(false);
        userRepository.save(driver);

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

        Company company = ride.getCompany();
        company.setReservedCredit(company.getReservedCredit().subtract(ride.getFare()));
        company.setOutstandingBalance(company.getOutstandingBalance().add(ride.getFare()));
        companyRepository.save(company);

        ride.setStatus(RideStatus.COMPLETED);

        driver.setAvailable(true);
        userRepository.save(driver);

        return rideRepository.save(ride);
    }

    public Ride updateRide(Long id, Ride rideDetails) {

        Ride ride = rideRepository.findById(id)
            .orElseThrow(() -> new RideNotFoundException("Ride not found"));
  
        ride.setPickupLocation(rideDetails.getPickupLocation());
        ride.setDropLocation(rideDetails.getDropLocation());

        return rideRepository.save(ride);
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