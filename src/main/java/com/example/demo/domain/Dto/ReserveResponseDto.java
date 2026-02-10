package com.example.demo.domain.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReserveResponseDto {

    private String name;

    private String phoneNumber;

    private String performanceName;

    private String venueAddress;

    private String seatNumber;

    private String reserveStatus;
}
