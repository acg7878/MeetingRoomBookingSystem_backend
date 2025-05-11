package com.example.meetingroombookingsystem.entity.dto.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("Users") // 指定数据库表名
@AllArgsConstructor
public class Users {

    @TableId(value = "user_id", type = IdType.AUTO)
    Integer userId;

    @TableField("username")
    String username;

    @TableField("password")
    String password;

    @TableField("email")
    String email;

    @TableField("created_at")
    Long createdAt;

    @TableField("updated_at")
    Long updatedAt;

    @TableField("status")
    String status;

}
