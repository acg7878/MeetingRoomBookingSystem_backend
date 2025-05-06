package com.example.meetingroombookingsystem.entity.dto.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Permissions") // 映射到数据库表名
public class Permissions {

    @TableId(value = "permission_id", type = IdType.AUTO) // 主键自增
    Integer permissionId;

    @TableField("permission_name")
    String permissionName;

    @TableField("description")
    String description;
}
