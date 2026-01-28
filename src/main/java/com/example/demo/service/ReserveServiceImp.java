package com.example.demo.service;

import com.example.demo.domain.*;
import com.example.demo.domain.Dto.PerformanceCreateRequestDto;
import com.example.demo.domain.Dto.PerformanceCreateResponseDto;
import com.example.demo.domain.Dto.ReserveRequestDto;
import com.example.demo.domain.Dto.ReserveResponseDto;
import com.example.demo.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReserveServiceImp implements ReserveService{

    private final PerformanceRepository performanceRepository;
    private final ScheduleRepository scheduleRepository;
    private final PerformanceSeatRepository performanceSeatRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public ReserveResponseDto reserve(ReserveRequestDto requestDto){
        String phoneNumber = requestDto.getPhoneNumber();
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);

        User reserveUser;
        if (user.isEmpty()){
            reserveUser = requestDto.toEntity(requestDto);
            userRepository.save(reserveUser);
        }
        else reserveUser = user.get();

        Optional<PerformanceSeat> seat = performanceSeatRepository.findById(requestDto.getSeatId());
        if (seat.isEmpty()) throw new IllegalArgumentException("Can not find Seat");
        PerformanceSeat performanceSeat = seat.get();
        Reservation reservation = getReservation(performanceSeat, reserveUser);
        reservationRepository.save(reservation);

        return ReserveResponseDto.builder()
                .schedule(performanceSeat.getSchedule())
                .seatNumber(performanceSeat.getSeatNumber())
                .reserveStatus("RESERVED")
                .build();
    }



    private static Reservation getReservation(PerformanceSeat seat, User reserveUser) {
        if (seat.getSeatStatus() != SeatStatus.AVAILABLE){
            throw new IllegalArgumentException("This Seat Already Reserved");
        }
        seat.setSeatStatus(SeatStatus.PENDING);

        return Reservation.builder()
                .user(reserveUser)
                .performanceSeat(seat)
                .reserveStatus("RESERVED")
                .reserveAt(LocalDateTime.now())
                .build();
    }
}

