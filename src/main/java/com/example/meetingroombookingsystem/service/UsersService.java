package com.example.meetingroombookingsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.meetingroombookingsystem.entity.dto.auth.Users;
import com.example.meetingroombookingsystem.entity.vo.request.ConfirmResetVO;
import com.example.meetingroombookingsystem.entity.vo.request.EmailRegisterVO;
import com.example.meetingroombookingsystem.entity.vo.request.EmailResetVO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsersService extends IService<Users>, UserDetailsService {
    Users findUsersByNameOrEmail(String username);
    String findRoleByUserId(Integer userId);
    String registerEmailVerifyCode(String type, String email, String address);
    String registerEmailAccount(EmailRegisterVO info);
    String resetEmailAccountPassword(EmailResetVO info);
    String resetConfirm(ConfirmResetVO info);
}
