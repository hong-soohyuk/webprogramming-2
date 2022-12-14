package com.example.userservice.service;

import com.example.userservice.dto.ReportDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.vo.RequestUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }


    @PostMapping
    public String createUser(UserDto userDto) {
        String email = userDto.getEmail();
        String pwd = userDto.getPwd();
        String name = userDto.getName();
        String address = userDto.getAddress();
        int defaultReportedCount = 0;
        return repository.insertUser(email, pwd, name, address,defaultReportedCount) ?
                "User is created successfully" : "There is a user registered with this email";
    }

    @GetMapping
    public ArrayList<UserEntity> getUsers() {
        return repository.getUsers();
    }

    @GetMapping
    public UserEntity getUser(String email, String pwd) {
        return repository.getUser(email);
    }

    public UserEntity getUser(String email) {
        return repository.getUser(email);
    }

    @PatchMapping
    public String updateUser(UserDto userDto) {
        String email = userDto.getEmail();
        String pwd = userDto.getPwd();
        String name = userDto.getName();
        UserEntity userEntity = repository.getUser(email);
        if (userEntity == null) return "This user is not exist";
        else {
            if (!pwd.equals(userEntity.getPwd())) return "Password is not matched";
            else {
                repository.updateUser(email, name);
                return "User is updated successfully";
            }
        }
    }

    @DeleteMapping
    public String deleteUser(UserDto userDto) {
        String email = userDto.getEmail();
        String pwd = userDto.getPwd();
        UserEntity userEntity = repository.getUser(email);
        if (userEntity == null) return "This user is not exist";
        else {
            if (!pwd.equals(userEntity.getPwd())) return "Password is not matched";
            else {
                repository.deleteUser(email);
                return "User is deleted successfully";
            }
        }
    }

    @PostMapping
    public String report(ReportDto reportDto) {
        String reporter = reportDto.getReporter();
        String pwd = reportDto.getPwd();
        String reportee = reportDto.getReportee();
        UserEntity reporterEntity = repository.getUser(reporter);
        UserEntity reporteeEntity = repository.getUser(reportee);
        if (reporterEntity == null || reporteeEntity == null) return "This user is not exist";
        else {
            if (!pwd.equals(reporterEntity.getPwd())) return "Password is not matched";
            else {
                if (repository.hasReport(reporter, reportee)) return "You have already reported this user";
                else {
                    repository.insertReport(reporter, reportee);
                    return "You have reported this user successfully";
                }
            }
        }
    }


}
