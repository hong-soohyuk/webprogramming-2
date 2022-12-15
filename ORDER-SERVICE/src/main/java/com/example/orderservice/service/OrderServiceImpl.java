package com.example.orderservice.service;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.jpa.OrderRepository;
import com.example.orderservice.jpa.STATUS;
import com.example.orderservice.vo.ResponseOrder;
import com.example.orderservice.vo.ResponseProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{
	private final OrderRepository repository;
	private final ProductServiceClient  productServiceClient;
	private final CircuitBreakerFactory circuitBreakerFactory;

	@Override
	public OrderDto	createOrder(OrderDto orderDetails) {
		orderDetails.setOrderId(UUID.randomUUID().toString());
		ModelMapper	modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		OrderEntity		orderEntity = modelMapper.map(orderDetails, OrderEntity.class);
		CircuitBreaker	circuitBreaker = circuitBreakerFactory.create("circuit breaker");
		ResponseProduct	responseProduct = circuitBreaker.run(() -> productServiceClient.getProduct(orderDetails.getProductId()),
				throwable -> null);
		if (responseProduct == null || responseProduct.getProductStatus() == "Banned" || responseProduct.getProductStatus() == "Sold")
			return (null);
		orderEntity.setProductName(responseProduct.getProductName());
		orderEntity.setStatus(STATUS.READY);
		repository.save(orderEntity);
		OrderDto		returnValue = modelMapper.map(orderEntity, OrderDto.class);
		return (returnValue);
	}

	@Override
	public OrderDto cancelOrder(String orderId) {
		OrderEntity	orderEntity = repository.findByOrderId(orderId);
		if (orderEntity.getStatus().equals(STATUS.READY))
		{
			orderEntity.setStatus(STATUS.CANCELED);
			repository.save(orderEntity);
			return (new ModelMapper().map(orderEntity, OrderDto.class));
		}
		else
			return (null);
	}

	@Override
	public OrderDto	getOrderByOrderId(String orderId) {return (new ModelMapper().map((OrderEntity)repository.findByOrderId(orderId), OrderDto.class));}

	@Override
	public Iterable<OrderEntity>	getOrderByUserEmail(String userEmail) {return (repository.findByUserEmail(userEmail));}
}
