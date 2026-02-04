package com.example.demo.domain.Dto;

import com.example.demo.domain.Performance;
import com.example.demo.domain.Venue;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ScheduleCreateResponseDto {
    private String performanceName;

    private String venueName;
    private String venueAddress;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
