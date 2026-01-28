package com.example.demo.infra;

import com.example.demo.domain.Dto.VenueCreateRequestDto;
import com.example.demo.domain.Dto.VenueCreateResponseDto;
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

    @PostMapping("/venue/add")
    public ResponseEntity<VenueCreateResponseDto> addVenue(@RequestBody VenueCreateRequestDto requestDto){
        VenueCreateResponseDto responseDto = managementService.createVenue(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/schedule/add")
    public ResponseEntity<?> createSchedule (){

        return ResponseEntity.ok();
    }

}
