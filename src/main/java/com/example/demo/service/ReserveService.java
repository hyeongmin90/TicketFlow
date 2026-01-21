package com.example.demo.service;

import com.example.demo.domain.Dto.ReserveRequestDto;
import com.example.demo.domain.Dto.ReserveResponseDto;

public interface ReserveService {
    ReserveResponseDto reserve(ReserveRequestDto request);
}
