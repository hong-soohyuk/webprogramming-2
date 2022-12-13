package com.example.shipmentservice.service;

import com.example.shipmentservice.dto.ShipmentDto;
import com.example.shipmentservice.jpa.ShipmentEntity;

public interface ShipmentService {
    ShipmentDto createShipment(ShipmentDto shipmentDetails);
    ShipmentDto getShipmentByShipmentId(String shipmentId);
    Iterable<ShipmentEntity> getShipmentsByUserId(String userId);

    ShipmentDto updateShipmentStatus(String shipmentId, String status);
}
