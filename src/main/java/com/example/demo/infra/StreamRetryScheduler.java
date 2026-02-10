package com.example.demo.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

import static com.example.demo.infra.Util.hashToDto;

@Component
@RequiredArgsConstructor
@Slf4j
public class StreamRetryScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedissonLockTicketFacade redissonLockTicketFacade;

    @Scheduled(fixedDelay = 10000)
    public void processPendingMessages(){
        var streamOps = redisTemplate.<String, String>opsForStream();

        String streamKey = "reserve:1";
        String groupName = "reserve-group";
        String consumerName = "retry-worker";

        PendingMessages pendingMessages = streamOps.pending(
                streamKey,
                groupName,
                Range.unbounded(),
                10L,
                Duration.ofSeconds(30)
        );

        RecordId[] recordIds = pendingMessages.stream()
                .map(PendingMessage::getId)
                .toArray(RecordId[]::new);

        List<MapRecord<String, String, String>> claimed = streamOps.claim(
                streamKey,
                groupName,
                consumerName,
                Duration.ofSeconds(30),
                recordIds
        );

        claimed.forEach(record->{
            log.info("Processing claimed message: {}", record.getId());
            redissonLockTicketFacade.reserveTicket(hashToDto(record));
            streamOps.acknowledge(streamKey, groupName, record.getId());
        });
    }
}
