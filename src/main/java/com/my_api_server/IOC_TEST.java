package com.my_api_server;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor //DI 자동 ANNOTATION
public class IOC_TEST {

    @Autowired
    private IOC ioc2;

    public IOC setIoc(IOC ioC) {
        ioc2 =ioc;
        return ioC;
    }

    public void IOC(IOC ioC) {
        ioc2 =ioC;
    } // == RequiredArgsConstructor

    private final IOC ioc;
    @GetMapping
    public void IOC_TEST() {
        ioc.func1();
    }
}
