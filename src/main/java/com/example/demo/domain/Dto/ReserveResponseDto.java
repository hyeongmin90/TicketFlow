package com.example.demo.domain.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReserveResponseDto {

    String TicketName;

    String TicketCode;

    String SeatNumber;

    String Status;
}
