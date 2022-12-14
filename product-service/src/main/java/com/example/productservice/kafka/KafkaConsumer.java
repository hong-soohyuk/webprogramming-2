package com.example.productservice.kafka;

import com.example.productservice.jpa.ProductEntity;
import com.example.productservice.jpa.Status;
import com.example.productservice.jpa.ProductRepository;
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
    ProductRepository repository;

    @Autowired
    public KafkaConsumer(ProductRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "order-product")
    @Transactional
    public void processMessage(String kafkaMessage) {

        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ProductEntity entity = repository.findByProductId((String) map.get("productId"));
        entity.setStatus(Status.SoldOut);

        repository.save(entity);
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
            List<ProductEntity> arr = repository.findByUserEmail((String) map.get("name"));
            for (ProductEntity productEntity : arr) {
                productEntity.setStatus(Status.Banned);
            }
        }

    }

}
