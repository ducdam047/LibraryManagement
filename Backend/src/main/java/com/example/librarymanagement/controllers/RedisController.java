package com.example.librarymanagement.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {

    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/string")
    public String testString() {
        redisTemplate.opsForValue().set("test:key", "hello-redis");
        return (String) redisTemplate.opsForValue().get("test:key");
    }
}
