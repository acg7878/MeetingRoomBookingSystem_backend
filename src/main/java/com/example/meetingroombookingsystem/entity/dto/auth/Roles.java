package com.example.meetingroombookingsystem.entity.dto.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Roles") // 数据库中的表名
public class Roles {

    @TableId(value = "role_id", type = IdType.AUTO) // 指定主键和主键策略
    Integer roleId;

    @TableField("role_name")
    String roleName;

    @TableField("description")
    String description;
}
