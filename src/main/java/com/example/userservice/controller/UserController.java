package com.example.userservice.controller;

import com.example.userservice.dto.ReportDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.service.KafkaProducer;
import com.example.userservice.service.UserService;
import com.example.userservice.service.UserServiceImpl;
import com.example.userservice.vo.RequestReport;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user-service")
public class UserController {

    // DB Connection
    private static final String db_url = "jdbc:oracle:thin:@localhost:1521:orcl";
    private static final String db_user = "c##USER_SERVICE_DB";
    private static final String db_password = "1234";

    private UserService service;
    private KafkaProducer kafkaProducer;

    @Autowired
    public UserController(KafkaProducer kafkaProducer) {
        UserRepository repository = new UserRepository();
        repository.connect(db_url, db_user, db_password);
        this.service = new UserServiceImpl(repository);
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/users")
    public String createUser(@RequestBody RequestUser requestUser) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        return service.createUser(userDto);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {
        List<UserEntity> userList = service.getUsers();
        List<ResponseUser> responseUsers = new ArrayList<>();
        for (UserEntity entity : userList) responseUsers.add(new ModelMapper().map(entity, ResponseUser.class));
        return ResponseEntity.status(HttpStatus.OK).body(responseUsers);
    }

    @GetMapping("/users/{email}/{pwd}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("email") String email, @PathVariable("pwd") String pwd) {
        return ResponseEntity.status(HttpStatus.OK).body(new ModelMapper()
                .map(service.getUser(email, pwd), ResponseUser.class));
    }

    @PatchMapping("/users")
    public String updateUser(@RequestBody RequestUser requestUser) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        return service.updateUser(userDto);
    }

    @DeleteMapping("/users")
    public String deleteUser(@RequestBody RequestUser requestUser) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(requestUser, UserDto.class);
        return service.deleteUser(userDto);
    }

    @PostMapping("/report")
    public String report(@RequestBody RequestReport requestReport) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ReportDto reportDto = mapper.map(requestReport, ReportDto.class);
        String message = service.report(reportDto);
        if (message.equals("You have reported this user successfully")) {
            mapper = new ModelMapper();
            mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            kafkaProducer.publish("UserReported", mapper.map(service.getUser(reportDto.getReportee()), UserDto.class));
        }
        return message;
    }

}
