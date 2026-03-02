package com.example.demo.domain.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSuccessEvent {
    private Long reservationId;
    private Long userId;
    private Long seatId;
    private Long amount;
    private LocalDateTime reservedAt;
}
