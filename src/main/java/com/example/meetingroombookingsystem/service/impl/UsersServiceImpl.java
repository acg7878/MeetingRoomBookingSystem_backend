package com.example.meetingroombookingsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.meetingroombookingsystem.entity.dto.auth.Roles;
import com.example.meetingroombookingsystem.entity.dto.auth.UserRoles;
import com.example.meetingroombookingsystem.entity.dto.auth.Users;
import com.example.meetingroombookingsystem.mapper.RolesMapper;
import com.example.meetingroombookingsystem.mapper.UserRolesMapper;
import com.example.meetingroombookingsystem.mapper.UsersMapper;
import com.example.meetingroombookingsystem.service.UsersService;

import jakarta.annotation.Resource;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

    @Resource
    private UserRolesMapper userRolesMapper;

    @Resource
    private RolesMapper rolesMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // users 和 user
        Users users = this.findUsersByNameOrEmail(username); // username可能是用户名也可能是邮箱
        if (users == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return User
                .withUsername(username)
                .password(users.getPassword())
                .roles(findRoleByUserId(users.getUserId()))
                .build();
    }

    public Users findUsersByNameOrEmail(String text) {
        return this.query()
                .eq("username", text).or()
                .eq("email", text)
                .one();
    }

    public String findRoleByUserId(Integer userId) {
        // 1. 通过 userId 查找 user_role 中对应的 roleId
        LambdaQueryWrapper<UserRoles> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRoles::getUserId, userId);
        UserRoles userRole = userRolesMapper.selectOne(wrapper);
        if (userRole == null) {
            return null;
        }
        // 2. 通过 roleId 查找 Roles 表
        Roles role = rolesMapper.selectById(userRole.getRoleId());
        return role != null ? role.getRoleName() : null;
    }

}