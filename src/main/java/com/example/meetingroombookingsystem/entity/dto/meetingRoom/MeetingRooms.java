package com.example.meetingroombookingsystem.entity.dto.meetingRoom;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@TableName("MeetingRooms") // 指定数据库表名
@AllArgsConstructor
public class MeetingRooms {

    @TableId(value = "room_id", type = IdType.AUTO)
    Integer roomId;

    @TableField("room_name")
    String roomName;

    @TableField("room_type")
    String roomType;

    @TableField("seat_count")
    Integer seatCount;

    @TableField("price_per_hour")
    Double pricePerHour;

    @TableField("status")
    String status;

    @TableField("created_at")
    Long createdAt;
}