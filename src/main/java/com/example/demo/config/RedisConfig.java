package com.example.demo.config;

import com.example.demo.infra.ReserveStreamListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

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
        public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer (
                RedisConnectionFactory connectionFactory,
                ReserveStreamListener streamListener,
                RedisTemplate<String, Object> redisTemplate){

                var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        .pollTimeout(Duration.ofSeconds(1))
                        .build();


                var container = StreamMessageListenerContainer.create(connectionFactory, options);

                String streamKey = "reserve:1";
                String groupName = "reserve-group";

                try {
                        redisTemplate.opsForStream()
                                .createGroup(streamKey, ReadOffset.from("0-0"), groupName);
                } catch (Exception ignored) {
                        // 이미 존재
                }

                container.receive(
                        Consumer.from(groupName, "worker-1"),
                        StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
                        streamListener
                );

                container.start();
                return container;
        }
}
