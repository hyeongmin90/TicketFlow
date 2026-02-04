package com.example.demo.infra;

import com.example.demo.domain.Dto.ReserveRequestDto;
import com.example.demo.domain.Dto.ReserveResponseDto;
import com.example.demo.domain.PerformanceSeat;
import com.example.demo.domain.SeatStatus;
import com.example.demo.domain.User;
import com.example.demo.domain.repository.PerformanceSeatRepository;
import com.example.demo.service.ReserveService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class RedissonLockTicketFacade {

    private final RedissonClient redissonClient;
    private final ReserveService reserveService;
    private final UserService userService;
    private final PerformanceSeatRepository performanceSeatRepository;

    public ReserveResponseDto reserveTicket(ReserveRequestDto requestDto) {
        User user = userService.userRegistration(requestDto);

        RLock lock = redissonClient.getLock("lock:seat:" + requestDto.getSeatId());

        try {
            boolean available = lock.tryLock(5, 3, TimeUnit.SECONDS);

            if (!available)
                throw new RuntimeException("락 획득 실패 - 이미 선택된 좌석입니다.");

            PerformanceSeat seat = performanceSeatRepository.findById(requestDto.getSeatId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석"));
            if (seat.getSeatStatus() != SeatStatus.AVAILABLE)
                throw new RuntimeException("이미 예매된 좌석");

            return reserveService.reserve(seat.getId(), user);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
