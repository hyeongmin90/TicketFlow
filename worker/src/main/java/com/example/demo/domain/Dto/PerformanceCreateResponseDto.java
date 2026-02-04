package com.example.demo.domain.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PerformanceCreateResponseDto {
    String name;

    LocalDateTime createAt;
}
