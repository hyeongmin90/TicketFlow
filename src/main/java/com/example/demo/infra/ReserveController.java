package com.example.demo.infra;

import com.example.demo.domain.Dto.ReserveRequestDto;
import com.example.demo.domain.Dto.ReserveResponseDto;
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

    private final RedissonLockTicketFacade redissonLockTicketFacade;

    @PostMapping("/reserve")
    public ResponseEntity<ReserveResponseDto> reserveTicket(@RequestBody ReserveRequestDto requestDto) {
        ReserveResponseDto reserveResponse = redissonLockTicketFacade.reserveTicket(requestDto);
        return ResponseEntity.ok(reserveResponse);
    }

}
