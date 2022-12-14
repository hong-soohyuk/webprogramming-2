package com.example.userservice.dto;

import com.example.userservice.vo.ResponseOrder;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
public class UserDto {

    private String email;
    private String pwd;
    private String name;
    private int reportedCount;

    private List<ResponseOrder> orders;


}
