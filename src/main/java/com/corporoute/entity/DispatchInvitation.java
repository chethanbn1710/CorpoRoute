package com.corporoute.entity;

import com.corporoute.enums.DispatchStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "ride_invitations")
@Data
public class DispatchInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ride_id")
    private Ride ride;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;

    @Enumerated(EnumType.STRING)
    private DispatchStatus status;

    private Integer dispatchRound;

    private LocalDateTime offeredAt;

    private LocalDateTime expiresAt;
}