package com.corporoute.dto;

import com.corporoute.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DriverDistance {

    private User driver;
    private double distance;
}