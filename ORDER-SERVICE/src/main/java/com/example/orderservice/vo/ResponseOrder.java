package com.example.orderservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseOrder {

    private String  productName;
    private Integer unitPrice;
    private Integer address;
    private String  orderId;
    private Data    createAt;
}
