package com.example.demo.service;

import com.example.demo.domain.*;
import com.example.demo.domain.Dto.*;
import com.example.demo.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManagementServiceImp implements ManagementService{

    private final SeatBatchRepository seatBatchRepository;
    private final VenueRepository venueRepository;
    private final PerformanceRepository performanceRepository;
    private final ScheduleRepository scheduleRepository;
    private final PerformanceSeatBatchRepository performanceSeatBatchRepository;

    @Override
    public VenueCreateResponseDto createVenue(VenueCreateRequestDto requestDto) {
        Venue venue = Venue.builder()
                .name(requestDto.getName())
                .address(requestDto.getAddress())
                .build();

        Venue savedVenue = venueRepository.save(venue);

        List<Seat> seats = new ArrayList<>();
        for(int row = 1; row <= requestDto.getRowCount(); row++){
            for(int col = 1; col <= requestDto.getColCount(); col++){
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
            sb.insert(0, (char)('A' + remainder));
            n = (n - 1) / 26;
        }
        return sb.toString();
    }

    @Override
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
    public ScheduleCreateResponseDto createSchedule(ScheduleCreateRequestDto requestDto) {
        Optional<Performance> performance = performanceRepository.findById(requestDto.getPerformanceId());
        Optional<Venue> venue = venueRepository.findById(requestDto.getVenueId());
        if(performance.isEmpty()) throw new IllegalArgumentException("Can not find performance");
        if(venue.isEmpty()) throw new IllegalArgumentException("Can not find venue");
        Venue venue1 = venue.get();
        Performance performance1 = performance.get();
        Schedule schedule = Schedule.builder()
                .venue(venue1)
                .performance(performance1)
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
                .build();

        scheduleRepository.save(schedule);

        List<Seat> seats = venue1.getSeats();
        List<PerformanceSeat> performanceSeats = new ArrayList<>();
        for(Seat seat : seats){
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
                .performanceName(performance1.getName())
                .venueName(venue1.getName())
                .venueAddress(venue1.getAddress())
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
                .build();
    }
}
