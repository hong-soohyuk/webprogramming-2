package com.example.productservice.service;

import com.example.productservice.dto.PatchProductRequest;
import com.example.productservice.dto.PostProductRequest;
import com.example.productservice.jpa.ProductEntity;
import com.example.productservice.jpa.Status;
import com.example.productservice.jpa.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository catalogRepository;

    private final Environment env;

    @Override
    public Iterable<ProductEntity> getAllCatalogs() {
        return catalogRepository.findAll();
    }

    @Override
    public void createProduct(PostProductRequest request, String userEmail) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ProductEntity orderEntity = modelMapper.map(request, ProductEntity.class);
        orderEntity.setStatus(Status.Selling);
        orderEntity.setUserEmail(userEmail);

        catalogRepository.save(orderEntity);
    }

    @Override
    @Transactional
    public void updateProduct(PatchProductRequest request) {

        ProductEntity productEntity = catalogRepository.findByProductId(request.getProductId());
        productEntity.setProductName(request.getProductName());
        productEntity.setPrice(request.getPrice());

    }

    @Override
    public ProductEntity getProduct(String productId) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        ProductEntity productEntity = catalogRepository.findByProductId(productId);
        return productEntity;
    }

    @Override
    public void deleteProduct(String productId) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        ProductEntity catalogEntity = catalogRepository.findByProductId(productId);

        catalogRepository.delete(catalogEntity);
    }


}
