package com.example.meetingroombookingsystem.controller;

import com.example.meetingroombookingsystem.entity.vo.RestBean;
import com.example.meetingroombookingsystem.service.EquipmentService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {
    @Resource
    EquipmentService equipmentService;

    @PreAuthorize("hasAuthority('View Equipment List')")
    @GetMapping("/list")
    public RestBean<List<String>> listAllEquipment() {
        return RestBean.success(equipmentService.listAllEquipment());
    }


    private <T> RestBean<T> messageHandle(Supplier<String> action){
        String message = action.get();
        if(message == null)
            return RestBean.success();
        else
            return RestBean.failure(400, message);
    }
}
