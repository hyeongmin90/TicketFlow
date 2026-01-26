package com.example.demo.service;

import com.example.demo.domain.Dto.ReleasePerformanceRequestDto;
import com.example.demo.domain.Dto.ReleasePerformanceResponseDto;
import com.example.demo.domain.Dto.ReserveRequestDto;
import com.example.demo.domain.Dto.ReserveResponseDto;
import com.example.demo.domain.Performance;
import com.example.demo.domain.PerformanceSeat;
import com.example.demo.domain.Reservation;
import com.example.demo.domain.User;
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
        
        Long seatId = requestDto.getSeatId();
        Optional<PerformanceSeat> seat = performanceSeatRepository.findById(seatId);
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

    @Override
    public ReleasePerformanceResponseDto releasePerformance(ReleasePerformanceRequestDto requestDto) {
        Performance performance = new Performance();
        performance.setName(requestDto.getName());
        performanceRepository.save(performance);
        return ReleasePerformanceResponseDto.builder()
                .name(requestDto.getName())
                .releaseAt(LocalDateTime.now())
                .build();
    }

    private static Reservation getReservation(PerformanceSeat seat, User reserveUser) {
        if (!seat.getStatus().equals("AVAILABLE")){
            throw new IllegalArgumentException("This Seat Already Reserved");
        }
        seat.setStatus("RESERVED");

        Reservation reservation = new Reservation();
        reservation.setUser(reserveUser);
        reservation.setPerformanceSeat(seat);
        reservation.setReserveStatus("RESERVED");
        return reservation;
    }
}

