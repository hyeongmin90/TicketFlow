package com.example.demo.domain.repository;

import com.example.demo.domain.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SeatBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsertSeats(List<Seat> seats){
        String sql = "INSERT INTO seat (venue_id, seat_row, seat_col, seat_number) VALUES (?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Seat seat = seats.get(i);
                ps.setLong(1, seat.getVenue().getId());
                ps.setInt(2, seat.getSeatRow());
                ps.setInt(3, seat.getSeatCol());
                ps.setString(4, seat.getSeatNumber());
            }

            @Override
            public int getBatchSize() {
                return seats.size();
            }
        });
    }
}
