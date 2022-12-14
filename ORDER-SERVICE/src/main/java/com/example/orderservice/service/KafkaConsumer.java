package com.example.orderservice.service;

import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.jpa.OrderRepository;
import com.example.orderservice.jpa.STATUS;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final OrderRepository   repository;

    @KafkaListener(topics = "shipment-updated", groupId = "group-id")
    public void processMessageOrderUpdated(String kafkaMessage){
        Map<Object, Object> map = new HashMap<>();
        ObjectMapper        mapper = new ObjectMapper();
        try     {map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>(){});}
        catch   (JsonProcessingException e){e.printStackTrace();}

        String      shipmentStatus = (String) map.get("status");
        String      orderId = (String) map.get("orderId");
        OrderEntity orderEntity = repository.findByOrderId(orderId);
        if (shipmentStatus.equals("SHIPPING"))
            orderEntity.setStatus(STATUS.SHIPPING);
        else if (shipmentStatus.equals("COMPLETED"))
            orderEntity.setStatus(STATUS.COMPLETED);
        repository.save(orderEntity);
    }
}
