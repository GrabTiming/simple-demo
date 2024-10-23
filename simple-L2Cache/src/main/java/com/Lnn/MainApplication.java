package com.Lnn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching //开启本地缓存
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class);
    }

}