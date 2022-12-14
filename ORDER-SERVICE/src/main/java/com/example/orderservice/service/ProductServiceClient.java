package com.example.orderservice.service;

import com.example.orderservice.vo.ResponseProduct;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductServiceClient {
    @GetMapping("product-service/products/{productId}")
    ResponseProduct getProduct(@PathVariable("productId") String productId);
}
