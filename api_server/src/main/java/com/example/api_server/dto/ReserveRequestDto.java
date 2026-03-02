package com.example.api_server.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReserveRequestDto {
    private String name;

    private String phoneNumber;

    private String password;

    private String seatId;
}
