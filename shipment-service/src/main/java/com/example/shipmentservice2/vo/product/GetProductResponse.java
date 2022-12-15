package com.example.shipmentservice2.vo.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetProductResponse {
    private String productId;
    private String productName;
    private Integer price;
}
