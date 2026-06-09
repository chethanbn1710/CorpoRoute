package com.corporoute.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.corporoute.enums.RideStatus;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "rides")
@Data
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;

    private Double pickupLatitude;
    private Double pickupLongitude;

    private Double dropLatitude;
    private Double dropLongitude;

    private BigDecimal fare;

    private Integer dispatchRound;

    private LocalDateTime dispatchStartedAt;

    @Enumerated(EnumType.STRING)
    private RideStatus status;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
}