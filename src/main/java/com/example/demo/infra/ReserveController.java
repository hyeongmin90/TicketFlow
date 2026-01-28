package com.example.demo.infra;

import com.example.demo.domain.Dto.PerformanceCreateRequestDto;
import com.example.demo.domain.Dto.PerformanceCreateResponseDto;
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

    private final ReserveServiceImp reserveService;

    @PostMapping("/reserve")
    public ResponseEntity<ReserveResponseDto> reserveTicket(@RequestBody ReserveRequestDto requestDto){
        ReserveResponseDto reserveResponse = reserveService.reserve(requestDto);
        return ResponseEntity.ok(reserveResponse);
    }



}
