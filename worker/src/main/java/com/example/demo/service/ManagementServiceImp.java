package com.example.demo.service;

import com.example.demo.domain.*;
import com.example.demo.domain.Dto.*;
import com.example.demo.domain.repository.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagementServiceImp implements ManagementService {

    private final SeatBatchRepository seatBatchRepository;
    private final VenueRepository venueRepository;
    private final PerformanceRepository performanceRepository;
    private final ScheduleRepository scheduleRepository;
    private final PerformanceSeatBatchRepository performanceSeatBatchRepository;

    @Override
    @Transactional
    public VenueCreateResponseDto createVenue(VenueCreateRequestDto requestDto) {
        Venue venue = Venue.builder()
                .name(requestDto.getName())
                .address(requestDto.getAddress())
                .build();

        Venue savedVenue = venueRepository.save(venue);

        List<Seat> seats = new ArrayList<>();
        for (int row = 1; row <= requestDto.getRowCount(); row++) {
            for (int col = 1; col <= requestDto.getColCount(); col++) {
                String seatNumber = String.format("%s-%d", toAlphabet(row), col);

                Seat seat = Seat.builder()
                        .seatNumber(seatNumber)
                        .seatRow(row)
                        .seatCol(col)
                        .venue(savedVenue)
                        .build();
                seats.add(seat);
            }
        }
        seatBatchRepository.batchInsertSeats(seats);

        return VenueCreateResponseDto.builder()
                .name(requestDto.getName())
                .address(requestDto.getAddress())
                .row(requestDto.getRowCount())
                .col(requestDto.getRowCount())
                .build();
    }

    private String toAlphabet(int n) {
        StringBuilder sb = new StringBuilder();
        while (n > 0) {
            int remainder = (n - 1) % 26;
            sb.insert(0, (char) ('A' + remainder));
            n = (n - 1) / 26;
        }
        return sb.toString();
    }

    @Override
    @Transactional
    public PerformanceCreateResponseDto createPerformance(PerformanceCreateRequestDto requestDto) {
        Performance performance = new Performance();
        performance.setName(requestDto.getName());
        performanceRepository.save(performance);
        return PerformanceCreateResponseDto.builder()
                .name(requestDto.getName())
                .createAt(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ScheduleCreateResponseDto createSchedule(ScheduleCreateRequestDto requestDto) {
        Performance performance = performanceRepository.findById(requestDto.getPerformanceId())
                .orElseThrow(() -> new IllegalArgumentException("Can not find performance"));
        Venue venue = venueRepository.findById(requestDto.getVenueId())
                .orElseThrow(() -> new IllegalArgumentException("Can not find venue"));

        Schedule schedule = Schedule.builder()
                .venue(venue)
                .performance(performance)
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
                .build();

        scheduleRepository.save(schedule);

        List<Seat> seats = venue.getSeats();
        List<PerformanceSeat> performanceSeats = new ArrayList<>();
        for (Seat seat : seats) {
            PerformanceSeat performanceSeat = PerformanceSeat.builder()
                    .schedule(schedule)
                    .seatNumber(seat.getSeatNumber())
                    .seatStatus(SeatStatus.AVAILABLE)
                    .price(requestDto.getTicketPrice())
                    .build();
            performanceSeats.add(performanceSeat);
        }

        performanceSeatBatchRepository.batchInsertPerformanceSeats(performanceSeats);

        return ScheduleCreateResponseDto.builder()
                .performanceName(performance.getName())
                .venueName(venue.getName())
                .venueAddress(venue.getAddress())
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
                .build();
    }
}
