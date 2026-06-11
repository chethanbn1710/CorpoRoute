package com.corporoute.repository;

import com.corporoute.entity.DispatchInvitation;
import com.corporoute.entity.Ride;
import com.corporoute.enums.DispatchStatus;
import com.corporoute.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DispatchInvitationRepository extends JpaRepository<DispatchInvitation, Long> {

    List<DispatchInvitation> findByDriverAndStatusAndExpiresAtAfter(
        User driver, DispatchStatus status, LocalDateTime now);

    Optional<DispatchInvitation> findById(Long id);

    List<DispatchInvitation> findByRide(Ride ride);

    boolean existsByRideAndDriver(Ride ride, User driver);

    boolean existsByRideAndDispatchRound(Ride ride, Integer dispatchRound);
        
}