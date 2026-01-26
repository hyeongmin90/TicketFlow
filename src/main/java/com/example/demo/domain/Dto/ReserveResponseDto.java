package com.example.demo.domain.Dto;

import com.example.demo.domain.Schedule;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReserveResponseDto {

    private Schedule schedule;

    private String seatNumber;

    private String reserveStatus;
}
