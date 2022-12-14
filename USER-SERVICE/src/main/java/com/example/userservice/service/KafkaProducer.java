package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducer {


    private KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String kafkaTopic, UserDto userDto) {
        try {
            kafkaTemplate.send(kafkaTopic, new ObjectMapper().writeValueAsString(userDto));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
