package com.example.meetingroombookingsystem.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.meetingroombookingsystem.entity.dto.meetingRoom.Equipments;
import com.example.meetingroombookingsystem.mapper.EquipmentsMapper;
import com.example.meetingroombookingsystem.service.EquipmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentsMapper, Equipments> implements EquipmentService {

    @Override
    public List<String> listAllEquipment() {
        return this.baseMapper.selectList(null)
                .stream()
                .map(Equipments::getEquipmentName)
                .toList();
    }
}
