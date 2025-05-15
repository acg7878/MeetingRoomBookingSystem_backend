package com.example.meetingroombookingsystem.controller;

import com.example.meetingroombookingsystem.entity.vo.RestBean;
import com.example.meetingroombookingsystem.entity.vo.response.user.UsersResponseVo;
import com.example.meetingroombookingsystem.service.UsersService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UsersService usersService;


    @PreAuthorize("hasAuthority('View User List')")
    @GetMapping("/list")
    public RestBean<List<UsersResponseVo>> listAllUsers() {
        return RestBean.success(usersService.listAllUsers());
    }

    @PreAuthorize("hasAuthority('Update User Status')")
    @PostMapping("/update-status")
    public RestBean<Void> updateUserStatus(@RequestBody Map<String, String> requestBody) {
        String userName = requestBody.get("userName");
        String status = requestBody.get("status");
        return this.messageHandle(() ->
                usersService.updateUserStatus(userName, status));
    }

    @PreAuthorize("hasAuthority('Delete User')")
    @GetMapping("/delete")
    public RestBean<Void> deleteUser (@RequestParam String userName) {
        return this.messageHandle(() -> usersService.deleteUser(userName));
    }

    private <T> RestBean<T> messageHandle(Supplier<String> action){
        String message = action.get();
        if(message == null)
            return RestBean.success();
        else
            return RestBean.failure(400, message);
    }
}
