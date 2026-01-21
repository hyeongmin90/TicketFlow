package com.example.demo.infra;

import com.example.demo.domain.Dto.TicketReserveRequestDto;
import com.example.demo.domain.Dto.TicketReserveResponseDto;
import com.example.demo.service.ReserveService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/reserve")
public class ReserveController {

    ReserveService ticketService;

    @PostMapping
    public ResponseEntity<TicketReserveResponseDto> reserveTicket(@RequestBody TicketReserveRequestDto request){
        TicketReserveResponseDto ticketReserveResponse = ticketService.reserve(request);
        return ResponseEntity.ok(ticketReserveResponse);
    }
}
