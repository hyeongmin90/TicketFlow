package com.example.api_server.service;

import com.example.api_server.dto.ReserveResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(60 * 1000L);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        try {
            emitter.send(SseEmitter.event().name("connect").data("connected!"));
        } catch (IOException e) {
            emitters.remove(userId);
        }
        return emitter;
    }

    public void sendToClient(ReserveResponseDto responseDto) {
        String userId = responseDto.getPhoneNumber();
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("reserve-result").data(responseDto));
                log.info("SSE 전송 성공: 유저 {}", userId);
            } catch (IOException e) {
                emitters.remove(userId);
                log.error("SSE 전송 실패: 유저 {}", userId);
            }
        }
    }
}
