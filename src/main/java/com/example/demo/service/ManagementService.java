package com.example.demo.service;

import com.example.demo.domain.Dto.*;

public interface ManagementService {
    VenueCreateResponseDto createVenue(VenueCreateRequestDto requestDto);

    PerformanceCreateResponseDto createPerformance(PerformanceCreateRequestDto requestDto);

    ScheduleCreateResponseDto createSchedule(ScheduleCreateRequestDto requestDto);
}
