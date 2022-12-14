package com.example.productservice.dto;

import com.example.productservice.jpa.Status;
import lombok.Data;

@Data
public class PatchProductRequest {

        private String productId;
        private String productName;
        private Status status;
        private Integer price;

}
