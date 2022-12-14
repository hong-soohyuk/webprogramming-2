package com.example.shipmentservice2.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseShipment {
    private String shipmentId;
    private String userEmail;
    private String orderId;
    private String productId;
    private String startAddress;
    private String endAddress;
    private String status;
}
