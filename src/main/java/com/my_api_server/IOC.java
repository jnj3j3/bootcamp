package com.my_api_server;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

//실제 스프링에게 빈으로 등록하게 해주는 설정
@Component
public class IOC {
    //@Bean
    public void func1(){
        System.out.println("fun1 실행");
    }
    public static void main(String[] args) {
        //메모리 jvm heap 메모리에 사용

        IOC ioc = new IOC();
        ioc.func1();
    }
}
