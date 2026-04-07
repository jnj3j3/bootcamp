package com.my_api_server.controller;

import com.my_api_server.service.OrderService;
import com.my_api_server.service.dto.OrderCreateDto;
import com.my_api_server.service.dto.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/vi/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public OrderResponseDto createOrder(@Validated @RequestBody OrderCreateDto dto) {
//        return orderService.createOrder(dto);
        return orderService.createOrderPLock(dto);
    }

    @GetMapping("/{id}")
    public OrderResponseDto findOrder(@PathVariable Long id) {
        return orderService.findOrder(id);
    }

    @PostMapping("/2")
    public OrderResponseDto createOrder2(@Validated @RequestBody OrderCreateDto dto) {
        return orderService.createOrderOptLock(dto);
    }
}
