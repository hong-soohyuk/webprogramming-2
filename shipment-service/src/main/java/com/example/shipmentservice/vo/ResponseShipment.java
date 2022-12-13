package com.example.shipmentservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseShipment {
    private String shipmentId;
    private String userId;
    private String orderId;
    private String status;
}
