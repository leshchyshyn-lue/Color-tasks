package com.example.colortasks.service;

import com.example.colortasks.config.RedisConfig;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
public class RedisService {

    private final RedisConfig redisConfig;

    public RedisService(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    public void deleteKeyBySessionId(String sessionId) {
        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConfig.redisConnectionFactory());
        Set<byte[]> patternResultConf = Objects.requireNonNull(
                redisTemplate.getConnectionFactory()).getConnection().keys(sessionId.getBytes()
        );
        if (Objects.nonNull(patternResultConf) && !patternResultConf.isEmpty()) {
            Objects.requireNonNull(
                    redisTemplate.getConnectionFactory()).getConnection().del(patternResultConf.toArray(new byte[0][]));
        }
    }

}
