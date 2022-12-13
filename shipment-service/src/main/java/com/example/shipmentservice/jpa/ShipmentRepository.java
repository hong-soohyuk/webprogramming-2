package com.example.shipmentservice.jpa;

import org.springframework.data.repository.CrudRepository;

public interface ShipmentRepository extends CrudRepository<ShipmentEntity, Long> {
    ShipmentEntity findByShipmentId(String shipmentId);
    Iterable<ShipmentEntity> findByUserId(String userId);
}
