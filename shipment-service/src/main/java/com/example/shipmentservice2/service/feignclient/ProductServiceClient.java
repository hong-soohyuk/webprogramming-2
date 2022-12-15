package com.example.shipmentservice2.service.feignclient;

import com.example.shipmentservice2.vo.product.GetProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("product-service")
public interface ProductServiceClient {

    @GetMapping("/product-service/products/{productId}")
    GetProductResponse getProduct(@PathVariable("productId") String productId);
}
