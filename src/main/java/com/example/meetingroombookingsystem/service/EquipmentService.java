package com.example.meetingroombookingsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.meetingroombookingsystem.entity.dto.meetingRoom.Equipments;

import java.util.List;

public interface EquipmentService extends IService<Equipments> {
    List<String> listAllEquipment();
}
