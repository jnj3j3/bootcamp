package com.my_api_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableResilientMethods
@EnableAsync
public class MyApiServerApplication {

    public static void main(String[] args) {

        String maxPoolSize = System.getProperty("jdk.virtualThreadScheduler.maxPoolSize");
        System.out.println("Max Pool Size: " + maxPoolSize);

// 현재 사용 가능한 코어 수 (기본 parallelism 값과 동일)
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("CPU Cores: " + cores);
        SpringApplication.run(MyApiServerApplication.class, args);
    }

}
