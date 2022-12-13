package com.example.shipmentservice.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShipmentDto implements Serializable {
    private String shipmentId;
    private String userId;
    private String orderId;
    private String status;
}
