package com.example.productservice.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductDto implements Serializable {
    private String productId;
    private Integer qty;
    private Integer price;
    private String orderId;
    private String userId;
}
