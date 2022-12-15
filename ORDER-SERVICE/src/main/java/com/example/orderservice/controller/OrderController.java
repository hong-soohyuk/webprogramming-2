package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.jpa.STATUS;
import com.example.orderservice.service.KafkaProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("/order-service")
public class OrderController {
    private final Environment   environment;
    private final OrderService  orderService;
    private final KafkaProducer kafkaProducer;

    @PostMapping(value = "/{userEmail}/order")
    public ResponseEntity<ResponseOrder>    createOrder(@PathVariable String userEmail, @RequestBody RequestOrder orderDetails){
        ModelMapper     modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderDto        orderDto = modelMapper.map(orderDetails, OrderDto.class);
        orderDto.setUserEmail(userEmail);
        OrderDto        createDto = orderService.createOrder(orderDto);
        if (createDto == null)
            return (ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseOrder()));

        ResponseOrder   returnValue = modelMapper.map(createDto, ResponseOrder.class);
        kafkaProducer.send("order-created-topic", createDto);
        return (ResponseEntity.status(HttpStatus.CREATED).body(returnValue));
    }

    @GetMapping(value = "/{userEmail}/order")
    public ResponseEntity<List<ResponseOrder>>  getOrder(@PathVariable("userEmail") String userEmail) {
        log.info("Before retrieve order data");
        Iterable<OrderEntity>   orderList = orderService.getOrderByUserEmail(userEmail);
        List<ResponseOrder>     result = new ArrayList<>();
        orderList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });
        log.info("Add retrieved order data");
        return (ResponseEntity.status(HttpStatus.OK).body(result));
    }
    @PutMapping("{userEmail}/order/{orderId}/cancel")
    public ResponseEntity<ResponseOrder>    cancelOrder(@PathVariable("userEmail") String userEmail,
                                                        @PathVariable("orderId") String orderId) {
        OrderDto    orderDto = orderService.cancelOrder(orderId);
        if (orderDto != null)
        {
            kafkaProducer.send("order-updated", orderDto);
            return (ResponseEntity.status(HttpStatus.OK).body(new ModelMapper().map(orderDto, ResponseOrder.class)));
        }
        else
            return (ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ModelMapper().map(new OrderDto(), ResponseOrder.class)));
    }
}
