package com.example.productservice.service;

import com.example.productservice.dto.PatchProductRequest;
import com.example.productservice.dto.PostProductRequest;
import com.example.productservice.jpa.ProductEntity;

public interface ProductService {
    public Iterable<ProductEntity> getAllCatalogs();
    public void createProduct(PostProductRequest request, String userEmail);

    void updateProduct(PatchProductRequest request);

    public ProductEntity getProduct(String productId);

    public void deleteProduct(String productId);


}
