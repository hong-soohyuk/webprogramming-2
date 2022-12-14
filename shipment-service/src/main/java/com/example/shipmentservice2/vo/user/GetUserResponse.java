package com.example.shipmentservice2.vo.user;

import lombok.Data;

@Data
public class GetUserResponse {
    private String email;
    private String name;
    private String address;
}
