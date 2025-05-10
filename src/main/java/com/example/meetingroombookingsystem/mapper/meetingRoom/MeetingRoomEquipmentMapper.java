package com.example.meetingroombookingsystem.mapper.meetingRoom;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.meetingroombookingsystem.entity.dto.meetingRoom.MeetingRoomEquipments;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MeetingRoomEquipmentMapper extends BaseMapper<MeetingRoomEquipments> {

    List<Integer> selectEquipmentIdsByRoomId(Integer roomId);
}
