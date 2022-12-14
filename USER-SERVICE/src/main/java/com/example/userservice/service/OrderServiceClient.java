package com.example.userservice.service;

import com.example.userservice.vo.ResponseOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("order-service")
public interface OrderServiceClient {

    @GetMapping("order-service/{userEmail}/order")
    List<ResponseOrder> getOrder(@PathVariable String userEmail);

}
