package com.example.demo.infra;

import com.example.demo.domain.Dto.ReservationSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // payment-request-topic
    private static final String TOPIC = "payment-request-topic";

    public void publishPaymentRequest(ReservationSuccessEvent event) {
        log.info("Publishing payment request event to Kafka: {}", event);
        kafkaTemplate.send(TOPIC, String.valueOf(event.getReservationId()), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Successfully sent event for reservationId=[{}] with offset=[{}]",
                                event.getReservationId(), result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send event for reservationId=[{}] due to: {}",
                                event.getReservationId(), ex.getMessage());
                    }
                });
    }
}
