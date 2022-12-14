package com.example.shipmentservice2.kafka;

import com.example.shipmentservice2.jpa.ShipmentEntity;
import com.example.shipmentservice2.jpa.ShipmentRepository;
import com.example.shipmentservice2.jpa.ShipmentStatus;
import com.example.shipmentservice2.service.feignclient.UserServiceClient;
import com.example.shipmentservice2.vo.user.GetUserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final ShipmentRepository repository;
    private final CircuitBreakerFactory circuitBreakerFactory;
    private final UserServiceClient userServiceClient;

    @KafkaListener(topics = "order-created-topic")
    public void processMessage(String kafkaMessage){
        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try{
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>(){});
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }
        String userEmail = (String) map.get("userEmail");
        ShipmentEntity shipmentEntity = new ShipmentEntity();
        shipmentEntity.setShipmentId(UUID.randomUUID().toString());
        shipmentEntity.setUserEmail(userEmail);
        shipmentEntity.setOrderId((String) map.get("orderId"));
        shipmentEntity.setProductId((String) map.get("productId"));
        shipmentEntity.setStatus(ShipmentStatus.READY);
        shipmentEntity.setEndAddress((String) map.get("endAddress"));

        // startAddress --> userId로 Address 가져오기 (with CircuitBreaker)
        log.info("Before call users microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        String responseUser = circuitBreaker.run(() -> userServiceClient.getUsers(userEmail).getAddress(),
                throwable -> "broken start address");
        log.info("After call users microservice");
        shipmentEntity.setStartAddress(responseUser);

        repository.save(shipmentEntity);
    }

    @KafkaListener(topics = "order-updated")
    public void processMessageOrderUpdated(String kafkaMessage){
        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try{
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>(){});
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }

        // Order가 취소됐을 때만 Process 진행
        String orderStatus = (String) map.get("status");
        if (orderStatus.equals("CANCELED")) {
            String orderId = (String) map.get("orderId");
            ShipmentEntity shipment = repository.findByOrderId(orderId);
            shipment.setStatus(ShipmentStatus.CANCELED);
            repository.save(shipment);
        }
    }
}
