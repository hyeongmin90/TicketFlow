package com.example.demo.domain.Dto;

import com.example.demo.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReserveRequestDto {
    private String name;

    private String phoneNumber;

    private String password;

    private Long seatId;

    public User toEntity(ReserveRequestDto requestDto){
        User user = new User();
        user.setName(requestDto.getName());
        user.setPhoneNumber(requestDto.getPhoneNumber());
        user.setPassword(requestDto.getPassword());
        return user;
    }
}
