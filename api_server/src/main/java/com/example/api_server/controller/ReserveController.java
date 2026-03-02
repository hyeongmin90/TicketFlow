package com.example.api_server.controller;

import com.example.api_server.dto.ReserveRequestDto;
import com.example.api_server.service.ReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ticket")
public class ReserveController {

    private final ReserveService reserveService;

    @PostMapping("/reserve")
    public void reserve(@RequestBody ReserveRequestDto requestDto){
        reserveService.reserve(requestDto);
    }
}
