package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.service.KafkaProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.service.ProductServiceClient;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import com.example.orderservice.vo.ResponseProduct;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order-service")
public class OrderController {
    private Environment             environment;
    private OrderService            orderService;
    private KafkaProducer           kafkaProducer;
    private ProductServiceClient    productServiceClient;

    public OrderController(Environment environment, OrderService orderService, KafkaProducer kafkaProducer) {
        this.environment = environment;
        this.orderService = orderService;
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping(value = "/{userEmail}/order")
    public ResponseEntity<ResponseOrder>    createOrder(@PathVariable String userEmail, @RequestBody RequestOrder orderDetails){
        ModelMapper     modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderDto        orderDto = modelMapper.map(orderDetails, OrderDto.class);
        orderDto.setUserEmail(userEmail);
        ResponseProduct product = productServiceClient.getProduct(orderDetails.getProductId());
        orderDto.setProductName(product.getProductName());
        OrderDto        createDto = orderService.createOrder(orderDto);
        ResponseOrder   returnValue = modelMapper.map(createDto, ResponseOrder.class);
        kafkaProducer.send("order-created-topic", orderDto);
        return (ResponseEntity.status(HttpStatus.CREATED).body(returnValue));
    }

    @GetMapping(value = "/{userEmail}/order")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userEmail") String userEmail) {
        log.info("Before retrieve order data");
        Iterable<OrderEntity>   orderList = orderService.getOrderByUserEmail(userEmail);
        List<ResponseOrder>     result = new ArrayList<>();
        orderList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });
        log.info("Add retrieved order data");
        return (ResponseEntity.status(HttpStatus.OK).body(result));
    }

}
