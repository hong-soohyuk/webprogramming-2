package com.example.orderservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseOrder {

    private String  productId;
    private String  productName;
    private Integer price;
    private String  status;
    private String  endAddress;
    private String  orderId;
    private Date    createAt;
}
