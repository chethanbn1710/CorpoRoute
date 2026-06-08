package com.corporoute.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "rides")
@Data
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeName;

    private String pickupLocation;

    private String dropLocation;

    private BigDecimal fare;

    private String status;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
}