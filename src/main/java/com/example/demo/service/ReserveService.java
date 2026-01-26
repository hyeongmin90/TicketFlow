package com.example.demo.service;

import com.example.demo.domain.Dto.ReleasePerformanceRequestDto;
import com.example.demo.domain.Dto.ReleasePerformanceResponseDto;
import com.example.demo.domain.Dto.ReserveRequestDto;
import com.example.demo.domain.Dto.ReserveResponseDto;

public interface ReserveService {
    ReserveResponseDto reserve(ReserveRequestDto requestDto);

    ReleasePerformanceResponseDto releasePerformance(ReleasePerformanceRequestDto requestDto);
}
