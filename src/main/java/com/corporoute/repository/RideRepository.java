package com.corporoute.repository;

import com.corporoute.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import com.corporoute.entity.User;
import com.corporoute.enums.RideStatus;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByEmployee(User employee);
    List<Ride> findByDriver(User driver);
    List<Ride> findByStatus(RideStatus status);
}