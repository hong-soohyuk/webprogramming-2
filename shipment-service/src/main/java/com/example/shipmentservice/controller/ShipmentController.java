package com.example.shipmentservice.controller;

import com.example.shipmentservice.dto.ShipmentDto;
import com.example.shipmentservice.jpa.ShipmentEntity;
import com.example.shipmentservice.service.ShipmentService;
import com.example.shipmentservice.vo.RequestShipment;
import com.example.shipmentservice.vo.ResponseShipment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/shipment-service")
@RequiredArgsConstructor
@Slf4j
public class ShipmentController {
    private final Environment env;
    private final ShipmentService shipmentService;
    private final KafkaProducer kafkaProducer;

    @PostMapping("{userId}/shipments")
    public ResponseEntity<ResponseShipment> createShipment(@PathVariable("userId") String userId,
                                                           @RequestBody RequestShipment shipmentDetails){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        ShipmentDto shipmentDto = modelMapper.map(shipmentDetails, ShipmentDto.class);
        shipmentDto.setUserId(userId);

        ShipmentDto createDto = shipmentService.createShipment(shipmentDto);
        ResponseShipment result = modelMapper.map(createDto, ResponseShipment.class);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("{userId}/shipments")
    public ResponseEntity<List<ResponseShipment>> getShipmentAll(@PathVariable("userId") String userId){
        Iterable<ShipmentEntity> shipmentList = shipmentService.getShipmentsByUserId(userId);

        List<ResponseShipment> result = new ArrayList<>();
        shipmentList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseShipment.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("{userId}/shipments/{shipmentId}")
    public ResponseEntity<ResponseShipment> getShipment(@PathVariable("userId") String userId, @PathVariable("shipmentId") String shipmentId){
        ShipmentDto shipmentDto = shipmentService.getShipmentByShipmentId(shipmentId);

        ResponseShipment result = new ModelMapper().map(shipmentDto, ResponseShipment.class);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PatchMapping("{userId}/shipments/{shipmentId}/status")
    public ResponseEntity<ResponseShipment> updateShipmentStatus(@PathVariable("userId") String userId,
                                                                 @PathVariable("shipmentId") String shipmentId,
                                                                 @RequestBody String status){
        ShipmentDto shipmentDto = shipmentService.updateShipmentStatus(shipmentId, status);

        ResponseShipment result = new ModelMapper().map(shipmentDto, ResponseShipment.class);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
