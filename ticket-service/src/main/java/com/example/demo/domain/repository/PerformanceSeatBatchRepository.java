package com.example.demo.domain.repository;

import com.example.demo.domain.PerformanceSeat;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PerformanceSeatBatchRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsertPerformanceSeats(List<PerformanceSeat> performanceSeats){
        String sql = "INSERT INTO performance_seat (schedule_id, seat_number, price, seat_status) VALUES (?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                PerformanceSeat performanceSeat = performanceSeats.get(i);
                ps.setLong(1, performanceSeat.getSchedule().getId());
                ps.setString(2, performanceSeat.getSeatNumber());
                ps.setLong(3, performanceSeat.getPrice());
                ps.setString(4, performanceSeat.getSeatStatus().name());
            }

            @Override
            public int getBatchSize() {
                return performanceSeats.size();
            }
        });
    }
}
