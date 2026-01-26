package com.example.demo.domain.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ReleasePerformanceResponseDto {
    String name;

    LocalDateTime releaseAt;
}
