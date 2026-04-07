package com.my_api_server.service.homework.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TimeUnit {
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}