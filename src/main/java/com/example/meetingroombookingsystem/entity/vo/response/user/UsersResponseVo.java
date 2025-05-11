package com.example.meetingroombookingsystem.entity.vo.response.user;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UsersResponseVo {
    private String username;
    private String email;
    private Long createdAt;
    private Long updatedAt;
    private String status;
}
