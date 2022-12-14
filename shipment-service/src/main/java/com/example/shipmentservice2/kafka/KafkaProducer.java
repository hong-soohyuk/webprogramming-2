package com.example.shipmentservice2.kafka;

import com.example.shipmentservice2.dto.ShipmentDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public ShipmentDto send(String kafkaTopic, ShipmentDto shipmentDto){
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(shipmentDto);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        kafkaTemplate.send(kafkaTopic, jsonInString);
        log.info("Kafka Producer send data from the Shipment microservice: " + shipmentDto);
        return shipmentDto;
    }
}
