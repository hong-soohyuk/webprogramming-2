package com.example.userservice.jpa;

import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class UserEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String pwd;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private int reportedCount;

    public UserEntity(String email, String pwd, String name, int reportedCount) {
        this.email = email;
        this.pwd = pwd;
        this.name = name;
        this.reportedCount = reportedCount;
    }

    public UserEntity() {
        this(null, null, null, -1);
    }
}
