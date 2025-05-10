package com.example.meetingroombookingsystem.entity.dto.meetingRoom;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@TableName("Equipments") // 指定数据库表名
@AllArgsConstructor
public class Equipments {
    @TableId(value = "equipment_id", type = IdType.AUTO)
    private Integer equipmentId;

    @TableField("equipment_name")
    private String equipmentName;
}
