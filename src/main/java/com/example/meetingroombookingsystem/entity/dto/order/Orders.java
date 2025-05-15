package com.example.meetingroombookingsystem.entity.dto.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("Orders") // 指定数据库表名
public class Orders {

    @TableId(value = "order_id", type = IdType.AUTO)
    private Integer orderId;

    @TableField("customer_id")
    private Integer customerId;

    @TableField("room_id")
    private Integer roomId;

    @TableField("start_time")
    private Long startTime;

    @TableField("end_time")
    private Long endTime;

    @TableField("total_price")
    private Double totalPrice;

    @TableField("payment_status")
    private String paymentStatus;

    @TableField("created_at")
    private Long createdAt;

    @TableField("updated_at")
    private Long updatedAt;
}