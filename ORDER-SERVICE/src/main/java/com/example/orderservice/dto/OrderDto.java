package com.example.orderservice.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class OrderDto implements Serializable {
	private String	productName;
	private	Integer	unitPrice;
	private String	address;
	private String	userId;
	private String	orderId;
}
