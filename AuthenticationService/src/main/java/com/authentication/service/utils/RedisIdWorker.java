package com.authentication.service.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@SuppressWarnings("all")
@RequiredArgsConstructor
public class RedisIdWorker {
    private final static long BEGIN_TIMESTAMP = 1640995200L;
    private final static int COUNT_BITS = 32;

    @Autowired
    private final StringRedisTemplate stringRedisTemplate;

    public long nextId(String keyPrefix) {
        LocalDateTime now = LocalDateTime.now();
        long nowSeconds = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSeconds - BEGIN_TIMESTAMP;
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        log.debug("Generating ID: prefix={}, date={}, timestamp={}", keyPrefix, date, timestamp);
        String redisKey = "icr:" + keyPrefix + ":" + date;
        long incrementCount = stringRedisTemplate.opsForValue().increment(redisKey);
        stringRedisTemplate.expire(redisKey, 24, TimeUnit.HOURS);
        long id = timestamp << COUNT_BITS | incrementCount;
        log.info("Generated ID: {}, timestamp={}, sequence={}", id, timestamp, incrementCount);
        return id;
    }

    public boolean isIdValidInRedis(long id, String keyPrefix, long maxAllowedSeconds) {
        long timestamp = id >> COUNT_BITS;
        long currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        long idGenerateTime = BEGIN_TIMESTAMP + timestamp;
        if (currentTime - idGenerateTime > maxAllowedSeconds) {
            return false;
        }
        LocalDateTime generateTime = LocalDateTime.ofEpochSecond(idGenerateTime, 0, ZoneOffset.UTC);
        String date = generateTime.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        String redisKey = "icr:" + keyPrefix + ":" + date;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey));
    }
}
