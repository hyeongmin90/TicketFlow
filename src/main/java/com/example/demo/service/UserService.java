package com.example.demo.service;

import com.example.demo.domain.Dto.ReserveRequestDto;
import com.example.demo.domain.User;

public interface UserService {
    User userRegistration(ReserveRequestDto requestDto);
}
