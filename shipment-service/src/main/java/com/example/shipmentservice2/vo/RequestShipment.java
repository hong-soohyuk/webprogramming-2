package com.example.shipmentservice2.vo;

import lombok.Data;

@Data
public class RequestShipment {
    private String orderId;
    private String productId;
    private String userEmail;
    private String startAddress;
    private String endAddress;
}
