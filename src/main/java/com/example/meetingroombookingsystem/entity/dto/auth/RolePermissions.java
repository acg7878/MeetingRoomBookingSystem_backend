package com.example.meetingroombookingsystem.entity.dto.auth;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("RolePermissions")
public class RolePermissions {

    @TableField("role_id")
    Integer roleId;

    @TableField("permission_id")
    Integer permissionId;
}
