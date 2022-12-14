package com.example.orderservice.jpa;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name="orders")
public class OrderEntity implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String	productId;
	@Column(nullable = false, length = 120)
	private String	productName;
	@Column(nullable = false)
	private Integer	unitPrice;
	@Column(nullable = false)
	private String	address;
	@Column(nullable = false)
	private String	userId;
	@Column(nullable = false, unique = true)
	private String	orderId;
	@Column(nullable = false, updatable = false, insertable = false)
	@ColumnDefault(value = "CURRENT_TIMESTAMP")
	private Date	createdAt;
}
