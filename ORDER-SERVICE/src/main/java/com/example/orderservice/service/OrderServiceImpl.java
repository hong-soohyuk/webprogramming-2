package com.example.orderservice.service;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.jpa.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService{
	@Autowired
	OrderRepository repository;

	@Override
	public OrderDto	createOrder(OrderDto orderDetails) {
		orderDetails.setOrderId(UUID.randomUUID().toString());
		ModelMapper	modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		OrderEntity	orderEntity = modelMapper.map(orderDetails, OrderEntity.class);
		repository.save(orderEntity);

		OrderDto	returnValue = modelMapper.map(orderEntity, OrderDto.class);
		return (returnValue);
	}

	@Override
	public OrderDto	getOrderByOrderId(String orderId) {return (new ModelMapper().map((OrderEntity)repository.findByOrderId(orderId), OrderDto.class));}

	@Override
	public Iterable<OrderEntity>	getOrderByUserId(String userId) {return (repository.findByUserId(userId));}
}
