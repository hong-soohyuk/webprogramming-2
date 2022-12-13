package com.example.productservice.dto;

import lombok.Data;

@Data
public class PatchProductRequest {

        private String productId;
        private String productName;
        private Integer stock;
        private Integer unitPrice;

}
