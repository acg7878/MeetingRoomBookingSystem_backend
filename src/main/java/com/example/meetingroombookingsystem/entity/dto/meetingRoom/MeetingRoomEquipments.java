package com.example.meetingroombookingsystem.entity.dto.meetingRoom;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("MeetingRoomEquipments")
public class MeetingRoomEquipments {
    @TableField("room_id")
    Integer roomId;

    @TableField("equipment_id")
    Integer equipmentId;
}
