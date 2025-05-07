package com.example.meetingroombookingsystem.entity.dto.auth;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@TableName("UserRoles") // 映射到数据库表 UserRoles
@AllArgsConstructor
public class UserRoles {

    @TableField("user_id") // 映射到数据库列 user_id
    Integer userId; // 用户 ID

    @TableField("role_id") // 映射到数据库列 role_id
    Integer roleId; // 角色 ID
}