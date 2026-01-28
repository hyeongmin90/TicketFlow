package com.example.demo.domain.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleCreateRequestDto {
    private Long performanceId;

    private Long venueId;

    private Long ticketPrice;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
