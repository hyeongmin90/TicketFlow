package com.example.demo;

import com.example.demo.domain.Dto.TicketReserveRequestDto;
import com.example.demo.domain.Dto.TicketReserveResponseDto;
import com.example.demo.domain.Ticket;
import com.example.demo.domain.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketService {

    TicketRepository ticketRepository;

    public TicketReserveResponseDto reserve(TicketReserveRequestDto request){
        Optional<Ticket>
        return new TicketReserveResponseDto();
    }
}

