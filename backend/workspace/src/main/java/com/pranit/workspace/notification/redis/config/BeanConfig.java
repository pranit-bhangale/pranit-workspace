package com.pranit.workspace.notification.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pranit.workspace.notification.redis.service.impl.RedisOneTimeTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.ott.OneTimeTokenService;

@Configuration
public class BeanConfig {

    @Bean
    public OneTimeTokenService redisOneTimeTokenService(StringRedisTemplate redisTemplate) {
        return new RedisOneTimeTokenService(redisTemplate);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
