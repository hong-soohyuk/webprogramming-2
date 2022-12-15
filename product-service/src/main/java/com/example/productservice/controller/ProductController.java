package com.example.productservice.controller;

import com.example.productservice.dto.GetProductDto;
import com.example.productservice.dto.PatchProductRequest;
import com.example.productservice.dto.PostProductRequest;
import com.example.productservice.jpa.ProductEntity;
import com.example.productservice.kafka.KafkaProducer;
import com.example.productservice.service.ProductService;
import com.example.productservice.vo.ResponseProduct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/product-service")
@RequiredArgsConstructor
public class ProductController {
    private final Environment env;
    private final ProductService productService;
    private final KafkaProducer kafkaProducer;

    @GetMapping("/products")
    public ResponseEntity<List<ResponseProduct>> getProducts() {
        Iterable<ProductEntity> productList = productService.getAllProducts();

        List<ResponseProduct> result = new ArrayList<>();
        productList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseProduct.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/products/{userEmail}")
    public String createProducts(@RequestBody PostProductRequest request, @PathVariable String userEmail) {
        return productService.createProduct(request, userEmail);
    }

    @PatchMapping("/products")
    public void updateProduct(@RequestBody PatchProductRequest request) {
        productService.updateProduct(request);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ResponseProduct> getCatalog(@PathVariable String productId) {
        ProductEntity catalog = productService.getProduct(productId);
        System.out.println(catalog.getProductName());
        ResponseProduct response = new ModelMapper().map(catalog, ResponseProduct.class);

        GetProductDto getCatalogDto = new GetProductDto();
        getCatalogDto.setProductId(productId);
        kafkaProducer.send("count-catalog-topic", getCatalogDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/products/{productId}")
    public void deleteProducts(@PathVariable String productId) {
        productService.deleteProduct(productId);
    }


}
