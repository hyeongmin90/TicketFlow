package com.example.demo.infra;

import com.example.demo.domain.Dto.ReserveRequestDto;
import org.springframework.data.redis.connection.stream.MapRecord;

import java.util.Map;

public class Util {

    public static ReserveRequestDto hashToDto(MapRecord<String, String, String> message){
        Map<String, String> hash = message.getValue();
        return ReserveRequestDto.builder()
                .name(hash.get("name"))
                .phoneNumber(hash.get("phoneNumber"))
                .password(hash.get("password"))
                .seatId(Long.valueOf(hash.get("seatId")))
                .build();
    }
}
