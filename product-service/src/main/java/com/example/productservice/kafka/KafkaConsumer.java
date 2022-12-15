package com.example.productservice.kafka;

import com.example.productservice.jpa.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class KafkaConsumer {
    ProductRepository productRepository;
    UserRepository userRepository;

    @Autowired
    public KafkaConsumer(ProductRepository productRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @KafkaListener(topics = "order-updated", groupId = "group1")
    @Transactional
    public void updateOrder(String kafkaMessage) {
        log.info("Order updated...!");
        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ProductEntity entity = productRepository.findByProductId((String) map.get("productId"));
        entity.setProductStatus(ProductEnum.Selling);

        productRepository.save(entity);
    }

    @KafkaListener(topics = "order-created-topic", groupId = "group1")
    @Transactional
    public void sellProduct(String kafkaMessage) {
        log.info("SellProduct method is called...!");
        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ProductEntity entity = productRepository.findByProductId((String) map.get("productId"));
        entity.setProductStatus(ProductEnum.Sold);
        productRepository.save(entity);
    }

    @KafkaListener(topics = "UserReported")
    @Transactional
    public void UserReported(String kafkaMessage) {

        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        Integer reportedCount = (Integer) map.get("reportedCount");
        if (reportedCount >= 5) {
            List<ProductEntity> arr = productRepository.findByUserEmail((String) map.get("name"));
            for (ProductEntity productEntity : arr) {
                productEntity.setProductStatus(ProductEnum.Banned);
                productRepository.save(productEntity);
            }
            UserEntity userEntity = userRepository.findByEmail((String) map.get("email"));
            userEntity.setBanned(true);
            userRepository.save(userEntity);
        }

    }

}
