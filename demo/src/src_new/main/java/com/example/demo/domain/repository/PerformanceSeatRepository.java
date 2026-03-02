package com.example.demo.domain.repository;

import com.example.demo.domain.PerformanceSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceSeatRepository extends JpaRepository<PerformanceSeat, Long> {

}
