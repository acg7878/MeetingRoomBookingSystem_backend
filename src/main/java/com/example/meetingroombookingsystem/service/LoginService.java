package com.example.meetingroombookingsystem.service;

import com.example.meetingroombookingsystem.entity.auth.UserRoles;
import com.example.meetingroombookingsystem.entity.auth.Users;
import com.example.meetingroombookingsystem.repository.auth.UserRepository;
import com.example.meetingroombookingsystem.repository.auth.UserRolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    @Autowired
    private UserRepository userRepository;
    private UserRolesRepository userRolesRepository;

    public Users login(String username, String password) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("找不到用户"));
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }
        return user;
    }

    public String getUserRole(Integer userId){
        UserRoles userRole = userRolesRepository.findByUser_userId(userId);
        if (userRole != null) {
            return userRole.getRole().getRole_name();
        } else {
            throw new RuntimeException("用户角色不存在");
        }
    }
}
