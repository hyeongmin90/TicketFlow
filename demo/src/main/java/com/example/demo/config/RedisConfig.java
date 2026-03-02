package com.example.demo.config;

import com.example.demo.infra.ReserveStreamListener;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.JacksonHashMapper;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

        @Value("${redis.stream.key}")
        private String streamKey;

        @Value("${redis.stream.group}")
        private String groupName;

        @Value("${redis.stream.consumer}")
        private String consumerName;

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
        public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer(
                        RedisConnectionFactory connectionFactory,
                        ReserveStreamListener streamListener,
                        RedisTemplate<String, Object> redisTemplate) {

                var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                                .builder()
                                .pollTimeout(Duration.ofSeconds(1))
                                .build();

                var container = StreamMessageListenerContainer.create(connectionFactory, options);

                try {
                        redisTemplate.opsForStream()
                                        .createGroup(streamKey, ReadOffset.from("0-0"), groupName);
                } catch (Exception ignored) {
                        // 이미 존재
                }

                container.receive(
                                Consumer.from(groupName, consumerName),
                                StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
                                streamListener);

                container.start();
                return container;
        }

        @Bean
        public JacksonHashMapper jacksonHashMapper(JsonMapper jsonMapper) {
                return new JacksonHashMapper(jsonMapper, false);
        }

}
