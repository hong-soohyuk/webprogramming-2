package com.example.shipmentservice2.jpa;

import org.springframework.data.repository.CrudRepository;

public interface ShipmentRepository extends CrudRepository<ShipmentEntity, Long> {
    ShipmentEntity findByShipmentId(String shipmentId);
    Iterable<ShipmentEntity> findByUserEmail(String userEmail);
    ShipmentEntity findByOrderId(String orderId);
}
