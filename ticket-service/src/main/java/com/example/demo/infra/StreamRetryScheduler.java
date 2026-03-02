package com.example.demo.infra;

import com.example.demo.service.RedissonLockTicketFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.List;

import static com.example.demo.infra.Util.hashToDto;

@Component
@RequiredArgsConstructor
@Slf4j
public class StreamRetryScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedissonLockTicketFacade redissonLockTicketFacade;

    @Value("${redis.stream.key}")
    private String streamKey;

    @Value("${redis.stream.group}")
    private String groupName;

    @Value("${redis.stream.consumer}")
    private String consumerName;

    @Scheduled(fixedDelay = 10000)
    public void processPendingMessages() {
        var streamOps = redisTemplate.<String, String>opsForStream();

        PendingMessages pendingMessages = streamOps.pending(
                streamKey,
                groupName,
                Range.unbounded(),
                10L,
                Duration.ofSeconds(30));

        RecordId[] recordIds = pendingMessages.stream()
                .map(PendingMessage::getId)
                .toArray(RecordId[]::new);

        List<MapRecord<String, String, String>> claimed = streamOps.claim(
                streamKey,
                groupName,
                consumerName,
                Duration.ofSeconds(30),
                recordIds);

        claimed.forEach(record -> {
            String messageId = String.valueOf(record.getId());
            String idempotencyKey = "processed:reserve:" + messageId;
            String retryCountKey = "retry:count:" + messageId;

            log.info("Processing claimed message: {}", record.getId());

            Boolean isNewRequest = redisTemplate.opsForValue()
                    .setIfAbsent(idempotencyKey, "REPROCESSING", Duration.ofDays(1));
            if (Boolean.FALSE.equals(isNewRequest)) {
                log.info("Already Processing Message: {}", messageId);
                streamOps.acknowledge(streamKey, groupName, record.getId());
                return;
            }

            try {
                redissonLockTicketFacade.reserveTicket(hashToDto(record), String.valueOf(record.getId()));
                streamOps.acknowledge(streamKey, groupName, record.getId());
                redisTemplate.delete(retryCountKey); // 성공 시 재시도 카운트 초기화
            } catch (Exception e) {
                log.error("Error Processing Message: {}", e.getMessage());
                redisTemplate.delete(idempotencyKey);

                // 재시도 횟수 증가 및 확인
                Long retryCount = redisTemplate.opsForValue().increment(retryCountKey);
                redisTemplate.expire(retryCountKey, Duration.ofDays(1)); // 카운트 키 만료 설정

                if (retryCount != null && retryCount >= 3) {
                    log.error("Max retries reached for message {}. Moving to DLQ.", messageId);
                    // DLQ로 메시지 이동
                    streamOps.add("reserve:dead-letter", record.getValue());
                    // 원본 스트림에서 ACK 처리하여 PEL에서 제거
                    streamOps.acknowledge(streamKey, groupName, record.getId());
                    // DLQ로 이동했으므로 retry count 삭제
                    redisTemplate.delete(retryCountKey);
                } else {
                    log.warn("Retry count for message {} is {}. Will retry later.", messageId, retryCount);
                }
            }
        });
    }
}
