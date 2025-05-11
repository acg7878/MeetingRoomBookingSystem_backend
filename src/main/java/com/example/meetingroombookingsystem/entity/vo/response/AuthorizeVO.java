package com.example.meetingroombookingsystem.entity.vo.response;

import lombok.Data;
import java.util.Date;

@Data
public class AuthorizeVO {
    String username;
    String role;
    String status;
    String token;
    Date expireTime; // 别动这个
}
