package com.example.demo.domain.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReserveResponseDto {

    private Long reservationId;

    private Long userId;

    private String name;

    private String phoneNumber;

    private String performanceName;

    private String venueAddress;

    private String seatNumber;

    private Long amount;

    private String reserveStatus;

    private java.time.LocalDateTime reservedAt;
}
