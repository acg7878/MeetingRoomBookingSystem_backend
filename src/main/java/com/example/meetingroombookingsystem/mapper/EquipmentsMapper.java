package com.example.meetingroombookingsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.meetingroombookingsystem.entity.dto.meetingRoom.Equipments;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EquipmentsMapper extends BaseMapper<Equipments> {
}
