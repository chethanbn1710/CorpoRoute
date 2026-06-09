package com.corporoute.dto;
import lombok.Data;

@Data
public class LocationUpdateRequest {

    private Double currentLatitude;
    private Double currentLongitude;

}