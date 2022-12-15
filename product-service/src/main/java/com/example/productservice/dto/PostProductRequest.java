package com.example.productservice.dto;

import lombok.Data;

@Data
public class PostProductRequest {
    private String productId;
    private String productName;
    private Integer unitPrice;
}
