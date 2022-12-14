package com.example.shipmentservice2.service;

import com.example.shipmentservice2.dto.ShipmentDto;
import com.example.shipmentservice2.jpa.ShipmentEntity;

public interface ShipmentService {
    ShipmentDto createShipment(ShipmentDto shipmentDetails);
    ShipmentDto getShipmentByShipmentId(String shipmentId);
    Iterable<ShipmentEntity> getShipmentsByUserId(String userId);

    ShipmentDto updateShipmentStatus(String shipmentId, String status);
}
