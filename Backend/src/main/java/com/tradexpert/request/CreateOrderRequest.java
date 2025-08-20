package com.tradexpert.request;

import com.tradexpert.domain.OrderType;

import lombok.Data;


@Data
public class CreateOrderRequest {
    private String coinId;
    private double quantity;
    private OrderType orderType;
}
