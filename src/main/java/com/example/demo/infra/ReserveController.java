package com.example.demo.infra;

import com.example.demo.domain.Dto.ReleasePerformanceRequestDto;
import com.example.demo.domain.Dto.ReleasePerformanceResponseDto;
import com.example.demo.domain.Dto.ReserveRequestDto;
import com.example.demo.domain.Dto.ReserveResponseDto;
import com.example.demo.service.ReserveServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ticket")
public class ReserveController {

    private final ReserveServiceImp ticketService;

    @PostMapping("/reserve")
    public ResponseEntity<ReserveResponseDto> reserveTicket(@RequestBody ReserveRequestDto requestDto){
        ReserveResponseDto reserveResponse = ticketService.reserve(requestDto);
        return ResponseEntity.ok(reserveResponse);
    }

    @PostMapping("/release/performance")
    public ResponseEntity<ReleasePerformanceResponseDto> releasePerformance(@RequestBody ReleasePerformanceRequestDto requestDto){
        ReleasePerformanceResponseDto releasePerformanceResponseDto = ticketService.releasePerformance(requestDto);
        return ResponseEntity.ok(releasePerformanceResponseDto);
    }

}
