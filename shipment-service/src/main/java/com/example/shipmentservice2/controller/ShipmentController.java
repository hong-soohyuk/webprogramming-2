package com.example.shipmentservice2.controller;

import com.example.shipmentservice2.dto.ShipmentDto;
import com.example.shipmentservice2.jpa.ShipmentEntity;
import com.example.shipmentservice2.kafka.KafkaProducer;
import com.example.shipmentservice2.service.ShipmentService;
import com.example.shipmentservice2.service.feignclient.ProductServiceClient;
import com.example.shipmentservice2.service.feignclient.UserServiceClient;
import com.example.shipmentservice2.vo.*;
import com.example.shipmentservice2.vo.product.GetProductResponse;
import com.example.shipmentservice2.vo.user.GetUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
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
    private final CircuitBreakerFactory circuitBreakerFactory;
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;

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
    public ResponseEntity<ResponseShipmentDetail> getShipment(@PathVariable("userEmail") String userEmail, @PathVariable("shipmentId") String shipmentId){
        ShipmentDto shipmentDto = shipmentService.getShipmentByShipmentId(shipmentId);
        ResponseShipmentDetail result = new ModelMapper().map(shipmentDto, ResponseShipmentDetail.class);

        // 데이터 통신 : getUser, getProduct
        log.info("Before call shipments microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitBreaker");
        GetUserResponse userResponse = circuitBreaker.run(() -> userServiceClient.getUsers(userEmail),
                throwable -> new GetUserResponse());
        GetProductResponse productResponse = circuitBreaker.run(() -> productServiceClient.getProduct(shipmentDto.getProductId()),
                throwable -> new GetProductResponse());
        log.info("After call shipments microservice");

        // 데이터 통신으로 가져온 값 response에 초기화
        result.setUserInfo(new ModelMapper().map(userResponse, UserInfo.class));
        result.setProductInfo(new ModelMapper().map(productResponse, ProductInfo.class));
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
    @DeleteMapping("{userId}/shipments/{shipmentId}/delete")
    public ResponseEntity<ResponseShipment> deleteShipment(@PathVariable("userId") String userId,
                                                                 @PathVariable("shipmentId") String shipmentId){
        ShipmentDto shipmentDto = shipmentService.deleteShipment(userId, shipmentId);
        ResponseShipment result = new ModelMapper().map(shipmentDto, ResponseShipment.class);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
