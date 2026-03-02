package com.example.api_server.service;

import com.example.api_server.dto.ReserveRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.JacksonHashMapper;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JacksonHashMapper jacksonHashMapper;

    public void reserve(ReserveRequestDto requestDto){
        sendToStream(requestDto);
    }

    private void sendToStream(ReserveRequestDto dto) {

        Map<String, Object> hash = jacksonHashMapper.toHash(dto);

//        ObjectMapper mapper = new ObjectMapper();
//        Map<String, Object> map = mapper.convertValue(dto,
//                new TypeReference<Map<String, Object>>() {});

        RecordId recordId = redisTemplate.opsForStream()
                .add("reserve:1", hash);
//        MapRecord<String, String, Object> record = StreamRecords.newRecord()
//                .ofMap(map)
//                .withStreamKey("reserve:1");

//        redisTemplate.opsForStream().add(record);

        log.info("Record sent to stream: {}", recordId);
    }


}
