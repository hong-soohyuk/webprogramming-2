package com.example.orderservice.vo;

import lombok.Data;

@Data
public class RequestOrder {
    private String  productName;
    private Integer unitPrice;
    private String  address;
}