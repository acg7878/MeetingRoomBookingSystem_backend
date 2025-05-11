package com.example.meetingroombookingsystem.entity.vo.response;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Data
public class OrderResponseVo {
    private Integer orderId;
    private String meetingRoomName;
    private Long startTime;
    private Long endTime;
    private Long createTime;
    private Long updateTime;
    private Double totalPrice;
    private String paymentStatus;
}
