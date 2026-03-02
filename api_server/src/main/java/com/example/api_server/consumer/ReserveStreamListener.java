package com.example.api_server.consumer;

import com.example.api_server.dto.ReserveResponseDto;
import com.example.api_server.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReserveStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final RedisTemplate<String, Object> streamRedisTemplate;
    private final SseService sseService;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        log.info("=== Message Received ===");
        log.info("Stream: {}", message.getStream());
        log.info("Record ID: {}", message.getId());

        ReserveResponseDto responseDto = hashToDto(message);

        log.info("Reservation details: {}", responseDto);
        processReserveResult(responseDto);

        streamRedisTemplate.opsForStream().acknowledge("reserve:result", "reserve-group", message.getId());
    }

    private ReserveResponseDto hashToDto(MapRecord<String, String, String> message){
        Map<String, String> hash = message.getValue();
        return ReserveResponseDto.builder()
                .userId(Long.valueOf(hash.get("userId")))
                .name(hash.get("name"))
                .phoneNumber(hash.get("phoneNumber"))
                .seatNumber(hash.get("seatNumber"))
                .performanceName(hash.get("performanceName"))
                .venueAddress(hash.get("venueAddress"))
                .reserveStatus(hash.get("reserveStatus"))
                .build();
    }

    private void processReserveResult(ReserveResponseDto responseDto){
        sseService.sendToClient(responseDto);
    }
}