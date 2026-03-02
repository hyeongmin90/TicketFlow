package com.example.demo.service;

import com.example.demo.domain.Dto.ReserveRequestDto;
import com.example.demo.domain.User;
import com.example.demo.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    @Override
    public User userRegistration(ReserveRequestDto requestDto) {
        String phoneNumber = requestDto.getPhoneNumber();
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);

        if (user.isEmpty()){
            User newUser = requestDto.toEntity(requestDto);
            userRepository.save(newUser);
            return newUser;
        }
        return user.get();
    }
}
