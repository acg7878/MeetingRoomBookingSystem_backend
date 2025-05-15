package com.example.meetingroombookingsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.meetingroombookingsystem.entity.dto.auth.Users;
import com.example.meetingroombookingsystem.entity.vo.request.auth.ConfirmResetVO;
import com.example.meetingroombookingsystem.entity.vo.request.auth.EmailRegisterVO;
import com.example.meetingroombookingsystem.entity.vo.request.auth.EmailResetVO;
import com.example.meetingroombookingsystem.entity.vo.response.meetingRoom.MeetingRoomResponseVo;
import com.example.meetingroombookingsystem.entity.vo.response.user.UsersResponseVo;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UsersService extends IService<Users>, UserDetailsService {
    Users findUsersByNameOrEmail(String username);
    String findRoleByUserId(Integer userId);
    String registerEmailVerifyCode(String type, String email, String address);
    String registerEmailAccount(EmailRegisterVO info);
    String resetEmailAccountPassword(EmailResetVO info);
    String resetConfirm(ConfirmResetVO info);
    List<UsersResponseVo> listAllUsers();
    String updateUserStatus(String userName, String status);

    String deleteUser(String userName);
}
