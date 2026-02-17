package com.example.demo.service;

import com.example.demo.domain.Dto.ReserveRequestDto;
import com.example.demo.domain.Dto.ReserveResponseDto;
import com.example.demo.domain.PerformanceSeat;
import com.example.demo.domain.SeatStatus;
import com.example.demo.domain.User;
import com.example.demo.domain.repository.PerformanceSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.JacksonHashMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedissonLockTicketFacade {

    private final RedissonClient redissonClient;
    private final ReserveService reserveService;
    private final UserService userService;
    private final PerformanceSeatRepository performanceSeatRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final JacksonHashMapper jacksonHashMapper;

    public void reserveTicket(ReserveRequestDto requestDto, String messageId) {
        String idempotencyKey = "processed:msg:" + messageId;

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

            ReserveResponseDto responseDto = reserveService.reserve(seat.getId(), user);

            redisTemplate.opsForValue().set(idempotencyKey, "COMPLETED", Duration.ofDays(1));

            sendToStream(responseDto);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
    private void sendToStream(ReserveResponseDto dto) {
        if(dto == null) return;
        Map<String, Object> hash = jacksonHashMapper.toHash(dto);
        RecordId recordId = redisTemplate.opsForStream().add("reserve:result", hash);

        log.info("Reserve result record sent to stream: {}", recordId);
    }
}
