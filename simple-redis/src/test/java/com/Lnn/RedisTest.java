package com.Lnn;

import org.apache.catalina.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void test1(){

        Object hello = "Hello";

        redisTemplate.opsForValue().set("hello",hello);

        redisTemplate.expire("hello", 1, TimeUnit.DAYS);

        System.out.println(redisTemplate.opsForValue().get("hello"));

    }

}
