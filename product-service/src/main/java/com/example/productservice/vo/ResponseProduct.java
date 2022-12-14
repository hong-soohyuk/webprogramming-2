package com.example.productservice.vo;

import com.example.productservice.jpa.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseProduct {
    private String productId;
    private String productName;
    private Status status;
    private String userEmail;
    private Integer price;
    private Date createdAt;
}
