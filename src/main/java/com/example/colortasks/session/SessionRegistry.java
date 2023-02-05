package com.example.colortasks.session;

import com.example.colortasks.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Component
public class SessionRegistry {
    private final ValueOperations<String, String> redisSessionStorage;

    @Autowired
    public SessionRegistry(final RedisTemplate<String, String> redisTemplate){
        this.redisSessionStorage = redisTemplate.opsForValue();

    }

    public String registerSession(final  String username) throws NotFoundException {
    if (username == null){
        throw new NotFoundException(("Username needs to be provided"));
    }
    final String sessionId = generateSessionId();
    redisSessionStorage.set(sessionId, username);
    return sessionId;
    }

    public String getUsernameForSession(final String sessionId){
            return redisSessionStorage.get(sessionId);
    }

    private String generateSessionId(){
        return new String(
                Base64.getEncoder().encode(
                        UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8))
        );
    }
}
