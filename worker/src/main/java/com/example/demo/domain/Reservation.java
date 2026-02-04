package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private PerformanceSeat performanceSeat;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    private String reserveStatus;

    private LocalDateTime reserveAt;
}
