package com.corporoute.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DispatchCandidate {

    private Long driverId;
    private String driverName;
    private Double distanceKm;
    private Long etaMinutes;
}