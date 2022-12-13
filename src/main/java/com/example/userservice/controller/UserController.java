package com.example.userservice.controller;

import com.example.userservice.dto.ReportDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.service.KafkaProducer;
import com.example.userservice.service.OrderServiceClient;
import com.example.userservice.service.UserService;
import com.example.userservice.service.UserServiceImpl;
import com.example.userservice.vo.RequestReport;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user-service")
public class UserController {

    private UserService userService;
    private KafkaProducer kafkaProducer;
    private OrderServiceClient orderServiceClient;

    @Autowired
    public UserController(KafkaProducer kafkaProducer, OrderServiceClient orderServiceClient) {
        this.userService = new UserServiceImpl();
        this.kafkaProducer = kafkaProducer;
        this.orderServiceClient = orderServiceClient;
    }

    @PostMapping("/users")
    public String createUser(@RequestBody RequestUser requestUser) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        return userService.createUser(userDto);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {
        List<UserEntity> userList = userService.getUsers();
        List<ResponseUser> responseUsers = new ArrayList<>();
        for (UserEntity entity : userList) {
            ResponseUser responseUser = new ModelMapper().map(entity, ResponseUser.class);
            responseUser.setOrders(orderServiceClient.getOrders(entity.getEmail()));
            responseUsers.add(responseUser);
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseUsers);
    }

    @GetMapping("/users/{email}/{pwd}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("email") String email, @PathVariable("pwd") String pwd) {
        UserEntity userEntity = userService.getUser(email, pwd);
        if (userEntity == null || !userEntity.getPwd().equals(pwd))
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseUser());
        else {
            ResponseUser responseUser = new ModelMapper()
                    .map(userEntity, ResponseUser.class);
            responseUser.setOrders(orderServiceClient.getOrders(email));
            return ResponseEntity.status(HttpStatus.OK).body(responseUser);
        }
    }

    @PatchMapping("/users")
    public String updateUser(@RequestBody RequestUser requestUser) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        return userService.updateUser(userDto);
    }

    @DeleteMapping("/users")
    public String deleteUser(@RequestBody RequestUser requestUser) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        return userService.deleteUser(userDto);
    }

    @PostMapping("/report")
    public String report(@RequestBody RequestReport requestReport) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ReportDto reportDto = mapper.map(requestReport, ReportDto.class);
        String message = userService.report(reportDto);
        if (message.equals("You have reported this user successfully")) {
            mapper = new ModelMapper();
            mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            kafkaProducer.publish("UserReported", mapper.map(userService.getUser(reportDto.getReportee()), UserDto.class));
        }
        return message;
    }

}
