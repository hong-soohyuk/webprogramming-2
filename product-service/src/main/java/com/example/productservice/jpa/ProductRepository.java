package com.example.productservice.jpa;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<ProductEntity, Long> {
    ProductEntity findByProductId(String productId);

    List<ProductEntity> findByUserEmail(String name);

}
