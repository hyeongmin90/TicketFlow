package com.example.demo.infra;

import com.example.demo.domain.Dto.ReserveRequestDto;
import com.example.demo.service.RedissonLockTicketFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;


import static com.example.demo.infra.Util.hashToDto;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReserveStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final RedissonLockTicketFacade redissonLockTicketFacade;
    private final RedisTemplate<String, Object> streamRedisTemplate;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try{
            log.info("=== Message Received ===");
            log.info("Stream: {}", message.getStream());
            log.info("Record ID: {}", message.getId());

            ReserveRequestDto dto = hashToDto(message);

            log.info("Name: {} Phone: {} Seat ID : {}", dto.getName(), dto.getPhoneNumber(), dto.getSeatId());
            processReservation(dto, String.valueOf(message.getId()));

            streamRedisTemplate.opsForStream().acknowledge("reserve:1", "reserve-group", message.getId());
        }
        catch (Exception e){
            log.error("처리 실패: {}", e.getMessage());
            throw new RuntimeException("Error" + e);
        }
    }


    public void processReservation(ReserveRequestDto dto, String messageId) {
        redissonLockTicketFacade.reserveTicket(dto, messageId);
    }
}
