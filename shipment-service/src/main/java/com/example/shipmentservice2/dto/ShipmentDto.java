package com.example.shipmentservice2.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ShipmentDto implements Serializable {
    private String shipmentId;
    private String userEmail;
    private String orderId;
    private String productId;
    private String startAddress;
    private String endAddress;
    private String status;
}
