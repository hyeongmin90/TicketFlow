package com.example.demo.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeatStatus {
    AVAILABLE("AVAIL", "예약 가능"),
    PENDING("PEND", "결제 대기중"),
    SOLD("SOLD", "판매 완료"),
    BLOCKED("BLOCK", "판매 중지");

    private final String dbCode;
    private final String description;
}