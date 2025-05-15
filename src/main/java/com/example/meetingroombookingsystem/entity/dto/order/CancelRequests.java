package com.example.meetingroombookingsystem.entity.dto.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;



@Data
@TableName("CancelRequests") // 映射数据库表名
public class CancelRequests {

    @TableId(value = "cancel_id", type = IdType.AUTO) // 主键映射
    private Integer cancelId;

    @TableField("order_id") // 映射字段
    private Integer orderId;

    @TableField("refund_status")
    private String refundStatus;


    @TableField("refund_amount")
    private Double refundAmount;

    @TableField("created_at")
    private Long createdAt;
}