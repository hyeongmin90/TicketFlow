package com.example.demo.service;

import com.example.demo.domain.*;
import com.example.demo.domain.Dto.ReserveResponseDto;
import com.example.demo.domain.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveServiceImp implements ReserveService {

    private final PerformanceSeatRepository performanceSeatRepository;
    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public ReserveResponseDto reserve(Long seatId, User user) {
        PerformanceSeat seat = performanceSeatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석"));
        if (seat.getSeatStatus() != SeatStatus.AVAILABLE)
            throw new IllegalArgumentException("This Seat Already Reserved");

        seat.setSeatStatus(SeatStatus.PENDING);
        performanceSeatRepository.save(seat);
        Reservation reservation = getReservation(seat, user);
        reservationRepository.save(reservation);

        return ReserveResponseDto.builder()
                .performanceName(seat.getSchedule().getPerformance().getName())
                .venueAddress(seat.getSchedule().getVenue().getAddress())
                .seatNumber(seat.getSeatNumber())
                .reserveStatus("RESERVED")
                .build();
    }

    private static Reservation getReservation(PerformanceSeat seat, User reserveUser) {

        seat.setSeatStatus(SeatStatus.PENDING);

        return Reservation.builder()
                .user(reserveUser)
                .performanceSeat(seat)
                .reserveStatus("RESERVED")
                .reserveAt(LocalDateTime.now())
                .build();
    }
}
