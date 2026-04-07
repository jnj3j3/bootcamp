package com.my_api_server.homework.common;

import com.my_api_server.service.dto.OrderCreateDto;

import java.util.ArrayList;
import java.util.List;

public class OrderCreateFixture {

    private Long id = 1234L;
    private List<Long> productIds = new ArrayList<>();
    private List<Long> counts = new ArrayList<>();

    public static OrderCreateFixture builder() {
        return new OrderCreateFixture();
    }

    public OrderCreateFixture id(Long id) {
        this.id = id;
        return this;
    }

    public OrderCreateFixture productIds(List<Long> productIds) {
        this.productIds = productIds;
        return this;
    }

    public OrderCreateFixture counts(List<Long> counts) {
        this.counts = counts;
        return this;
    }

    public OrderCreateDto build() {
        return new OrderCreateDto(id, productIds, counts);
    }
}