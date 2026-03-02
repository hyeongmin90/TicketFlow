package com.example.api_server.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class ReserveResponseDto {

    private Long userId;

    private String name;

    private String phoneNumber;

    private String performanceName;

    private String venueAddress;

    private String seatNumber;

    private String reserveStatus;
}
