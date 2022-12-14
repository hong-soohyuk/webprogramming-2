package com.example.userservice.service;

import com.example.userservice.dto.ReportDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;

import java.util.ArrayList;

public interface UserService {

    String createUser(UserDto userDto);

    ArrayList<UserEntity> getUsers();

    UserEntity getUser(String email, String pwd);

    UserEntity getUser(String email);

    String updateUser(UserDto userDto);

    String deleteUser(UserDto userDto);

    String report(ReportDto reportDto);
}
