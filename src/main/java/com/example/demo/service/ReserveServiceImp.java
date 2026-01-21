package com.example.demo.service;

import com.example.demo.domain.Dto.ReserveRequestDto;
import com.example.demo.domain.Dto.ReserveResponseDto;
import com.example.demo.domain.repository.PerformanceSeatRepository;
import com.example.demo.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReserveServiceImp implements ReserveService{

    private final PerformanceSeatRepository performanceSeatRepository;
    private final UserRepository userRepository;

    @Override
    public ReserveResponseDto reserve(ReserveRequestDto request){
        return new ReserveResponseDto();
    }
}

