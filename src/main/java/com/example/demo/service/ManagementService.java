package com.example.demo.service;

import com.example.demo.domain.Dto.VenueCreateRequestDto;
import com.example.demo.domain.Dto.VenueCreateResponseDto;

public interface ManagementService {
    VenueCreateResponseDto createVenue(VenueCreateRequestDto requestDto);

    void createSchedule();
}
