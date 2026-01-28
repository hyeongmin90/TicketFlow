package com.example.demo.service;

import com.example.demo.domain.Dto.VenueCreateRequestDto;
import com.example.demo.domain.Dto.VenueCreateResponseDto;
import com.example.demo.domain.Seat;
import com.example.demo.domain.Venue;
import com.example.demo.domain.repository.SeatBatchRepository;
import com.example.demo.domain.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagementServiceImp implements ManagementService{

    private final SeatBatchRepository seatBatchRepository;
    private final VenueRepository venueRepository;

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
    public void createSchedule() {

    }
}
