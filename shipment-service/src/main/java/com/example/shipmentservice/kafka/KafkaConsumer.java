package com.example.shipmentservice.kafka;

import com.example.shipmentservice.jpa.ShipmentEntity;
import com.example.shipmentservice.jpa.ShipmentRepository;
import com.example.shipmentservice.jpa.ShipmentStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final ShipmentRepository repository;

    @KafkaListener(topics = "order-created-topic")
    public void processMessage(String kafkaMessage){
        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try{
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>(){});
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }
        ShipmentEntity shipmentEntity = new ShipmentEntity();
        shipmentEntity.setShipmentId(UUID.randomUUID().toString());
        shipmentEntity.setUserId((String) map.get("userId"));
        shipmentEntity.setOrderId((String) map.get("orderId"));
        shipmentEntity.setStatus(ShipmentStatus.READY.name());

        repository.save(shipmentEntity);
        log.info("Order-Created-Topic - Shipment Created");
    }
}
