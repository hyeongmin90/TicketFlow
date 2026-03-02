package com.example.demo.infra;

import com.example.demo.domain.Dto.ReserveRequestDto;
import com.example.demo.service.RedissonLockTicketFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReserveStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final RedissonLockTicketFacade redissonLockTicketFacade;
    private final RedisTemplate<String, Object> streamRedisTemplate;
    private final JsonMapper jsonMapper;

    @Value("${redis.stream.key}")
    private String streamKey;

    @Value("${redis.stream.group}")
    private String groupName;

    @Value("${redis.stream.consumer}")
    private String consumerName;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try {
            log.info("=== Message Received ===");
            log.info("Stream: {}", message.getStream());
            log.info("Record ID: {}", message.getId());

            ReserveRequestDto requestDto = convertToDto(message);
            processReservation(requestDto, String.valueOf(message.getId()));

            streamRedisTemplate.opsForStream().acknowledge(streamKey, groupName, message.getId());
        } catch (Exception e) {
            log.error("메시지 처리 실패: {}", e.getMessage());
        }
    }

    private ReserveRequestDto convertToDto(MapRecord<String, String, String> message) throws Exception {
        Map<String, String> rawData = message.getValue();
        Map<String, Object> messageMap = new HashMap<>(rawData);

        String json = jsonMapper.writeValueAsString(messageMap);
        ReserveRequestDto requestDto = jsonMapper.readValue(json, ReserveRequestDto.class);
        log.info("ReserveRequestDto: {}", requestDto);
        return requestDto;
    }

    public void processReservation(ReserveRequestDto dto, String messageId) {
        redissonLockTicketFacade.reserveTicket(dto, messageId);
    }
}
