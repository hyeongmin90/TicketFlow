package com.example.demo.infra;

import com.example.demo.domain.Dto.ReserveRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReserveStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final RedissonLockTicketFacade redissonLockTicketFacade;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        try{
            log.info("=== Message Received ===");
            log.info("Stream: {}", message.getStream());
            log.info("Record ID: {}", message.getId());

            Map<String, String> hash = message.getValue();

            ReserveRequestDto dto = ReserveRequestDto.builder()
                    .name(hash.get("name"))
                    .phoneNumber(hash.get("phoneNumber"))
                    .password(hash.get("password"))
                    .seatId(Long.valueOf(hash.get("seatId")))
                    .build();

            log.info("Name: {} Phone: {} Seat ID : {}", dto.getName(), dto.getPhoneNumber(), dto.getSeatId());
            processReservation(dto);
        }
        catch (Exception e){
            throw new RuntimeException("Error" + e);
        }

    }

    private void processReservation(ReserveRequestDto dto) {
        redissonLockTicketFacade.reserveTicket(dto);
    }

}
