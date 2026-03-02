package com.example.demo.infra;

import com.example.demo.domain.Dto.*;
import com.example.demo.service.ManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/manage")
public class ManagementController {

    private final ManagementService managementService;

    @PostMapping("/venue/create")
    public ResponseEntity<VenueCreateResponseDto> createVenue(@RequestBody VenueCreateRequestDto requestDto){
        VenueCreateResponseDto responseDto = managementService.createVenue(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/performance/create")
    public ResponseEntity<PerformanceCreateResponseDto> createPerformance(@RequestBody PerformanceCreateRequestDto requestDto){
        PerformanceCreateResponseDto releasePerformanceResponseDto = managementService.createPerformance(requestDto);
        return ResponseEntity.ok(releasePerformanceResponseDto);
    }

    @PostMapping("/schedule/create")
    public ResponseEntity<ScheduleCreateResponseDto> createSchedule (@RequestBody ScheduleCreateRequestDto requestDto){
        ScheduleCreateResponseDto responseDto = managementService.createSchedule(requestDto);
        return ResponseEntity.ok(responseDto);
    }

}
