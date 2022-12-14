package com.example.shipmentservice2.controller;

import com.example.shipmentservice2.dto.ShipmentDto;
import com.example.shipmentservice2.jpa.ShipmentEntity;
import com.example.shipmentservice2.kafka.KafkaProducer;
import com.example.shipmentservice2.service.ShipmentService;
import com.example.shipmentservice2.vo.RequestShipment;
import com.example.shipmentservice2.vo.ResponseShipment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
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

    @PostMapping("{userEmail}/shipments")
    public ResponseEntity<ResponseShipment> createShipment(@PathVariable("userEmail") String userEmail,
                                                           @RequestBody RequestShipment shipmentDetails){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        ShipmentDto shipmentDto = modelMapper.map(shipmentDetails, ShipmentDto.class);
        shipmentDto.setUserEmail(userEmail);

        ShipmentDto createDto = shipmentService.createShipment(shipmentDto);
        ResponseShipment result = modelMapper.map(createDto, ResponseShipment.class);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("{userEmail}/shipments")
    public ResponseEntity<List<ResponseShipment>> getShipmentAll(@PathVariable("userEmail") String userEmail){
        Iterable<ShipmentEntity> shipmentList = shipmentService.getShipmentsByUserId(userEmail);

        List<ResponseShipment> result = new ArrayList<>();
        shipmentList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseShipment.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("{userEmail}/shipments/{shipmentId}")
    public ResponseEntity<ResponseShipment> getShipment(@PathVariable("userEmail") String userEmail, @PathVariable("shipmentId") String shipmentId){
        ShipmentDto shipmentDto = shipmentService.getShipmentByShipmentId(shipmentId);

        ResponseShipment result = new ModelMapper().map(shipmentDto, ResponseShipment.class);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PatchMapping("{userId}/shipments/{shipmentId}/status")
    public ResponseEntity<ResponseShipment> updateShipmentStatus(@PathVariable("userId") String userId,
                                                                 @PathVariable("shipmentId") String shipmentId,
                                                                 @RequestBody String status){
        ShipmentDto shipmentDto = shipmentService.updateShipmentStatus(shipmentId, status);
        kafkaProducer.send("shipment-updated", shipmentDto);

        ResponseShipment result = new ModelMapper().map(shipmentDto, ResponseShipment.class);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
