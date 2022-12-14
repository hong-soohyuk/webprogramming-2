package com.example.productservice.dto;

import com.example.productservice.jpa.Status;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProductDto implements Serializable {
    private String productId;
    private String productName;
    private Status status;
    private Integer price;
    private String userEmail;
}
