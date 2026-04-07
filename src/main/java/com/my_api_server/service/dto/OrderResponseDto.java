package com.my_api_server.service.dto;


import com.my_api_server.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(staticName = "of")
@Builder
public class OrderResponseDto {
    private LocalDateTime orderCompletedTime;
    private OrderStatus orderStatus;
    private boolean isSuccess;
}
