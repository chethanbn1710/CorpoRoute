package com.corporoute.entity;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "companies")
@Data
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal creditLimit;

    private BigDecimal outstandingBalance;

    @Column(nullable = false)
    private BigDecimal reservedCredit = BigDecimal.ZERO;

    @OneToMany(mappedBy = "company")
    @JsonIgnore
    private List<User> users;
}