package com.example.orderservice.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class OrderDto implements Serializable {
	private String	productId;
	private String	productName;
	private	Integer	price;
	private String	address;
	private String	userEmail;
	private String	orderId;
}
