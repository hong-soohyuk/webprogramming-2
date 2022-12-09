package com.example.userservice.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
public class ReportDto {
    private String reporter;
    private String pwd;
    private String reportee;
}
