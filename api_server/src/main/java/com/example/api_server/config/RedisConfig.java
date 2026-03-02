package com.example.api_server.config;

import com.example.api_server.consumer.ReserveStreamListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.JacksonHashMapper;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final JsonMapper jsonMapper;

    @Bean
    public RedisTemplate<String, Object> streamRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);

        return template;
    }

    @Bean
    public JacksonHashMapper jacksonHashMapper() {
        return new JacksonHashMapper(jsonMapper, false);
    }

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer (
            RedisConnectionFactory connectionFactory,
            ReserveStreamListener streamListener,
            RedisTemplate<String, Object> redisTemplate){

        var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        .pollTimeout(Duration.ofSeconds(1))
                        .build();


        var container = StreamMessageListenerContainer.create(connectionFactory, options);

        String streamKey = "reserve:result";
        String groupName = "API-group";

        try {
            redisTemplate.opsForStream()
                    .createGroup(streamKey, ReadOffset.from("0-0"), groupName);
        } catch (Exception ignored) {
            // 이미 존재
        }

        container.receive(
                Consumer.from(groupName, "API-1"),
                StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
                streamListener
        );

        container.start();
        return container;
    }
}
