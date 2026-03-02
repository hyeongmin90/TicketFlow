package com.example.demo.service;

import com.example.demo.domain.Dto.ReserveRequestDto;
import com.example.demo.domain.Performance;
import com.example.demo.domain.PerformanceSeat;
import com.example.demo.domain.Schedule;
import com.example.demo.domain.SeatStatus;
import com.example.demo.domain.Venue;
import com.example.demo.domain.repository.PerformanceRepository;
import com.example.demo.domain.repository.PerformanceSeatRepository;
import com.example.demo.domain.repository.ReservationRepository;
import com.example.demo.domain.repository.ScheduleRepository;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.domain.repository.VenueRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReserveServiceConcurrencyTest {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private PerformanceSeatRepository performanceSeatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private RedissonLockTicketFacade redissonLockTicketFacade;

    @Autowired
    private VenueRepository venueRepository;

    private Long seatId;

    @BeforeEach
    void setUp() {
        // 테스트용 데이터 세팅
        Performance performance = performanceRepository.save(Performance.builder()
                .name("testPerformance")
                .build());

        Venue venue = venueRepository.save(Venue.builder()
                .name("testVenue")
                .address("testAddress")
                .build());

        Schedule schedule = scheduleRepository.save(Schedule.builder()
                .performance(performance)
                .venue(venue)
                .startTime(java.time.LocalDateTime.now())
                .endTime(java.time.LocalDateTime.now().plusHours(2))
                .build());

        PerformanceSeat seat = performanceSeatRepository.save(PerformanceSeat.builder()
                .seatNumber("A1")
                .price(10000L)
                .seatStatus(SeatStatus.AVAILABLE)
                .schedule(schedule)
                .build());

        seatId = seat.getId();
    }

    @AfterEach
    void tearDown() {
        reservationRepository.deleteAll();
        userRepository.deleteAll();
        performanceSeatRepository.deleteAll();
        scheduleRepository.deleteAll();
        performanceRepository.deleteAll();
        venueRepository.deleteAll();
    }

    @Test
    @DisplayName("100 user reserve same seat at the same time")
    void reserve_concurrency_test() throws InterruptedException {
        // given
        int threadCount = 100;
        // 멀티스레드 환경을 위한 ExecutorService (비동기 작업을 단순하게 처리할 수 있도록 해주는 자바 API)
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // 100개의 스레드가 동시에 요청을 보내기 위해 CountDownLatch 사용 (다른 스레드에서 수행하는 작업이 완료될 때까지 대기)
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger logicExCount = new AtomicInteger(0); // IllegalArgumentException ('이미 예약된 좌석')
        AtomicInteger dbExCount = new AtomicInteger(0); // DataIntegrityViolationException (DB 제약조건 위배)

        // when
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    ReserveRequestDto request = ReserveRequestDto.builder()
                            .seatId(seatId)
                            .phoneNumber("010-1234-" + String.format("%04d", finalI)) // 각기 다른 유저 번호
                            .name("user" + finalI)
                            .build();

                    redissonLockTicketFacade.reserveTicket(request, "TestId" + finalI);
                    // reserveService.reserve(request);
                    successCount.incrementAndGet();
                } catch (IllegalArgumentException e) {
                    logicExCount.incrementAndGet();
                } catch (Exception e) {
                    dbExCount.incrementAndGet();
                    System.out.println("Exception: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드의 작업이 끝날 때까지 대기

        long reservationCount = reservationRepository.count();
        PerformanceSeat seat = performanceSeatRepository.findById(seatId).orElseThrow();

        System.out.println("Success: " + successCount.get());
        System.out.println("Logic Exception(Already Reserved): " + logicExCount.get());
        System.out.println("DB Exception(Concurrency Conflict): " + dbExCount.get());
        System.out.println("Total DB Data: " + reservationCount);

        // then
        // 동시성 제어가 없으면 1명만 성공하는게 아니라 여러명이 성공하게 됨 -> 테스트 실패 예상
        // 하지만 우리는 "문제 재현"이 목적이므로, 1이 아니면 메시지를 출력하도록 하거나,
        // 1이 아닐 것임(문제 상황)을 검증하는 방식보다는 일단 1과 같은지 검증해서 Fail을 띄우는게 일반적입니다.
        assertThat(successCount.get()).isEqualTo(1);
    }
}
