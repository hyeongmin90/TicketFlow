package com.example.demo.service;

import com.example.demo.domain.Dto.PerformanceCreateRequestDto;
import com.example.demo.domain.Dto.PerformanceCreateResponseDto;
import com.example.demo.domain.Dto.ReserveRequestDto;
import com.example.demo.domain.Dto.ReserveResponseDto;
import com.example.demo.domain.PerformanceSeat;
import com.example.demo.domain.User;

public interface ReserveService {
    ReserveResponseDto reserve(Long seatId, User user);

}
