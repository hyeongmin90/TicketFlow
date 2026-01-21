package com.example.demo.service;

import com.example.demo.domain.Dto.TicketReserveRequestDto;
import com.example.demo.domain.Dto.TicketReserveResponseDto;
import com.example.demo.domain.PerformanceSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReserveService {

    PerformanceSeatRepository ticketRepository;

    public TicketReserveResponseDto reserve(TicketReserveRequestDto request){
        return new TicketReserveResponseDto();
    }
}

