package com.example.meetingroombookingsystem.entity.vo.response.order;

import lombok.Data;

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
