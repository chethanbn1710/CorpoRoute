package com.corporoute.service;

import org.springframework.stereotype.Service;

import com.corporoute.dto.DispatchCandidate;
import com.corporoute.dto.DispatchInvitationResponse;
import com.corporoute.dto.DriverDistance;
import com.corporoute.entity.*;
import com.corporoute.enums.*;
import com.corporoute.exception.*;
import com.corporoute.repository.*;
import com.corporoute.util.DistanceCalculator;


import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final CompanyRepository companyRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final DispatchInvitationRepository dispatchInvitationRepository;

    public RideService(RideRepository rideRepository, CompanyRepository companyRepository,
            UserService userService, UserRepository userRepository, 
            DispatchInvitationRepository dispatchInvitationRepository) {

        this.rideRepository = rideRepository;
        this.companyRepository = companyRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.dispatchInvitationRepository = dispatchInvitationRepository;
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
        ride.setDispatchStartedAt(LocalDateTime.now());

        Ride savedRide = rideRepository.save(ride);
        generateDispatchInvitations(savedRide.getId());

        return savedRide;
    }

    public int getCurrentDispatchRound(Long rideId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        long elapsedSeconds = Duration.between(
                ride.getDispatchStartedAt(),
                LocalDateTime.now()
        ).getSeconds();

        int round = (int)(elapsedSeconds / 15) + 1;
        return Math.min(round, 4);
    }

    public void generateDispatchInvitations(Long rideId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        int round = getCurrentDispatchRound(rideId);

        List<DispatchCandidate> candidates = getDispatchRound(rideId);

        for (DispatchCandidate candidate : candidates) {

            User driver = userRepository.findById(candidate.getDriverId())
                    .orElseThrow(() ->new UserNotFoundException("Driver not found"));

            DispatchInvitation invitation = new DispatchInvitation();

            invitation.setRide(ride);
            invitation.setDriver(driver);

            invitation.setStatus(DispatchStatus.OFFERED);
            invitation.setDispatchRound(round);
            invitation.setOfferedAt(LocalDateTime.now());
            invitation.setExpiresAt(LocalDateTime.now().plusSeconds(15));

            dispatchInvitationRepository.save(invitation);
        }
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

    public List<DispatchCandidate> findNearestDrivers(Long rideId, int limit) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        List<User> availableDrivers = userRepository.findByRoleAndAvailable(Role.DRIVER, true);

        List<DriverDistance> driverDistances = new ArrayList<>();

        for (User driver : availableDrivers) {

            if (driver.getCurrentLatitude() == null || driver.getCurrentLongitude() == null) {
                continue;
            }

            double distance = DistanceCalculator.calculateDistance(
                    driver.getCurrentLatitude(), driver.getCurrentLongitude(),
                    ride.getPickupLatitude(), ride.getPickupLongitude());

            driverDistances.add(new DriverDistance(driver, distance));
        }

        driverDistances.sort(Comparator.comparingDouble(DriverDistance::getDistance));

        return driverDistances.stream().limit(limit).map(dd -> {

            User driver = dd.getDriver();
            double distance = Math.round(dd.getDistance() * 1000.0) / 1000.0;
            long eta = Math.round((distance / 30.0) * 60);

            return new DispatchCandidate(
                    driver.getId(),
                    driver.getName(),
                    distance,
                    eta
            );
        }).toList();
    }

    public List<DispatchCandidate> getDispatchRound(Long rideId) {

        int limit;
        int round = getCurrentDispatchRound(rideId);
        switch (round) {
            case 1 -> limit = 1;
            case 2 -> limit = 2;
            case 3 -> limit = 3;
            default -> limit = Integer.MAX_VALUE;
        }

        return findNearestDrivers(rideId, limit);
    }

    public List<DispatchInvitationResponse> getPendingInvitations(String driverEmail) {

        User driver = userService.getUserByEmail(driverEmail);

        List<DispatchInvitation> invitations =
                dispatchInvitationRepository.findByDriverAndStatusAndExpiresAtAfter(
                driver, DispatchStatus.OFFERED, LocalDateTime.now());

        return invitations.stream().map(invitation -> {

            Ride ride = invitation.getRide();

            return new DispatchInvitationResponse(
                invitation.getId(), ride.getId(),
                ride.getEmployee().getName(),
                ride.getPickupLatitude(),
                ride.getPickupLongitude(),
                ride.getFare(),
                invitation.getDispatchRound());
        })
        .toList();
    }

    public long calculateETA(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        User driver;

        if (ride.getDriver() != null) {
            driver = ride.getDriver();
        } else {
            DispatchCandidate candidate = findNearestDrivers(rideId, 1).get(0);
            driver = userService.getUserById(candidate.getDriverId());
        }

        double distanceKm = DistanceCalculator.calculateDistance(
                driver.getCurrentLatitude(), driver.getCurrentLongitude(),
                ride.getPickupLatitude(), ride.getPickupLongitude());

        double averageSpeedKmph = 30.0;
        double etaHours = distanceKm / averageSpeedKmph;
        return Math.round(etaHours * 60);
    }

    public Ride acceptInvitation(Long invitationId, String driverEmail) {

        DispatchInvitation invitation = dispatchInvitationRepository
                .findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        User driver = userService.getUserByEmail(driverEmail);

        if (!invitation.getDriver().getId().equals(driver.getId())) {
            throw new RuntimeException("This invitation does not belong to you");
        }

        if (invitation.getStatus() != DispatchStatus.OFFERED) {
            throw new RuntimeException("Invitation is no longer active");
        }

        Ride ride = invitation.getRide();
        ride.setDriver(driver);
        ride.setStatus(RideStatus.ACCEPTED);
        rideRepository.save(ride);

        invitation.setStatus(DispatchStatus.ACCEPTED);
        dispatchInvitationRepository.save(invitation);


        List<DispatchInvitation> invitations =
            dispatchInvitationRepository.findByRide(ride);

        for (DispatchInvitation other : invitations) {

            if (!other.getId().equals(invitation.getId())
                    && other.getStatus() == DispatchStatus.OFFERED) {

                other.setStatus(DispatchStatus.EXPIRED);
                dispatchInvitationRepository.save(other);
            }
        }

        return ride;
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
  
        ride.setPickupLatitude(rideDetails.getPickupLatitude());
        ride.setPickupLongitude(rideDetails.getPickupLongitude());

        ride.setDropLatitude(rideDetails.getDropLatitude());
        ride.setDropLongitude(rideDetails.getDropLongitude());

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