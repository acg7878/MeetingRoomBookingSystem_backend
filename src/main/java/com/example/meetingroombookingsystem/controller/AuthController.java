package com.example.meetingroombookingsystem.controller;

import com.example.meetingroombookingsystem.dto.request.LoginRequest;
import com.example.meetingroombookingsystem.entity.auth.Users;
import com.example.meetingroombookingsystem.service.LoginService;
import com.example.meetingroombookingsystem.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping
    public String login(@RequestBody LoginRequest loginRequest) {
        Users user = loginService.login(loginRequest.getUsername(),loginRequest.getPassword());
        String role = loginService.getUserRole(user.getUserId());
        UserDetails userDetails = User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(role)
                .build();
        return jwtUtils.createJWT(userDetails, user.getUserId(), user.getUsername());
    }
}
