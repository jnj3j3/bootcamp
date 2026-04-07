package com.my_api_server.service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ProductResponseDto {
    private String productName;

    private Long price;

    private Long stock;
}
