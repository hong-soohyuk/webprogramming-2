package com.example.productservice.service;

import com.example.productservice.dto.PatchProductRequest;
import com.example.productservice.dto.PostProductRequest;
import com.example.productservice.jpa.ProductEntity;
import com.example.productservice.jpa.ProductEnum;
import com.example.productservice.jpa.ProductRepository;
import com.example.productservice.jpa.UserRepository;
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
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private final Environment env;

    @Override
    public Iterable<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public String createProduct(PostProductRequest request, String userEmail) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ProductEntity orderEntity = modelMapper.map(request, ProductEntity.class);
        if (userRepository.findByEmail(userEmail).isBanned()) {
            return "You are banned";
        }
        if (productRepository.findByProductId(orderEntity.getProductId()) != null) {
            return "Already exists";
        }
        orderEntity.setProductStatus(ProductEnum.Selling);
        orderEntity.setUserEmail(userEmail);
        productRepository.save(orderEntity);
        return "Your product is registered";
    }

    @Override
    @Transactional
    public void updateProduct(PatchProductRequest request) {

        ProductEntity productEntity = productRepository.findByProductId(request.getProductId());
        productEntity.setProductName(request.getProductName());
        productEntity.setPrice(request.getPrice());
        productRepository.save(productEntity);
    }

    @Override
    public ProductEntity getProduct(String productId) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        ProductEntity productEntity = productRepository.findByProductId(productId);
        return productEntity;
    }

    @Override
    public void deleteProduct(String productId) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        ProductEntity catalogEntity = productRepository.findByProductId(productId);

        productRepository.delete(catalogEntity);
    }


}
