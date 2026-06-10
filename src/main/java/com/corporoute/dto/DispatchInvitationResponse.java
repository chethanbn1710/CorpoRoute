package com.corporoute.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DispatchInvitationResponse {

    private Long invitationId;
    private Long rideId;

    private String employeeName;

    private Double pickupLatitude;
    private Double pickupLongitude;

    private BigDecimal fare;

    private Integer dispatchRound;
}
