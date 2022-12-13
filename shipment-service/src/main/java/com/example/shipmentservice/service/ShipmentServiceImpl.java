package com.example.shipmentservice.service;

import com.example.shipmentservice.dto.ShipmentDto;
import com.example.shipmentservice.jpa.ShipmentEntity;
import com.example.shipmentservice.jpa.ShipmentRepository;
import com.example.shipmentservice.jpa.ShipmentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentServiceImpl implements ShipmentService{

    private final ShipmentRepository shipmentRepository;
    private final Environment env;

    @Override
    public ShipmentDto createShipment(ShipmentDto shipmentDetails) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        shipmentDetails.setShipmentId(UUID.randomUUID().toString());
        shipmentDetails.setStatus(ShipmentStatus.READY.name());
        ShipmentEntity shipmentEntity = modelMapper.map(shipmentDetails, ShipmentEntity.class);

        shipmentRepository.save(shipmentEntity);

        ShipmentDto response = modelMapper.map(shipmentEntity, ShipmentDto.class);
        return response;
    }

    @Override
    public ShipmentDto getShipmentByShipmentId(String shipmentId) {
        ShipmentEntity shipmentEntity = shipmentRepository.findByShipmentId(shipmentId);
        ShipmentDto shipmentDto = new ModelMapper().map(shipmentEntity, ShipmentDto.class);
        return shipmentDto;
    }

    @Override
    public Iterable<ShipmentEntity> getShipmentsByUserId(String userId) {
        return shipmentRepository.findByUserId(userId);
    }

    @Override
    public ShipmentDto updateShipmentStatus(String shipmentId, String status) {
        ShipmentEntity shipmentEntity = shipmentRepository.findByShipmentId(shipmentId);
        shipmentEntity.setStatus(status);
        ShipmentDto shipmentDto = new ModelMapper().map(shipmentEntity, ShipmentDto.class);
        return shipmentDto;
    }
}
