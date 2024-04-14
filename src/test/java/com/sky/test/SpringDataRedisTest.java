package com.sky.test;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class SpringDataRedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void TestredisTemplate(){
        System.out.println(redisTemplate);
    }
    @Test
    public void TestRedis(){
        redisTemplate.opsForValue().set("name","小明");
        String name = (String) redisTemplate.opsForValue().get("name");
        System.out.println(name);
    }

}
