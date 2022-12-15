package com.example.shipmentservice2.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseShipmentDetail {
    private String shipmentId;
    private String orderId;
    private UserInfo userInfo;
    private ProductInfo productInfo;
    private String startAddress;
    private String endAddress;
    private String status;
}
