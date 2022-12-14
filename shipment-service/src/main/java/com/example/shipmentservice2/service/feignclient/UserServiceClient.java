package com.example.shipmentservice2.service.feignclient;

import com.example.shipmentservice2.vo.user.GetUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/user-service/users/{userEmail}")
    GetUserResponse getUsers(@PathVariable("userEmail") String userEmail);
}
