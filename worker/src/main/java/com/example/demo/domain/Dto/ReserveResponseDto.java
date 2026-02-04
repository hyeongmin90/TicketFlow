package com.example.demo.domain.Dto;

import com.example.demo.domain.Schedule;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReserveResponseDto {

    private String performanceName;

    private String venueAddress;

    private String seatNumber;

    private String reserveStatus;
}
