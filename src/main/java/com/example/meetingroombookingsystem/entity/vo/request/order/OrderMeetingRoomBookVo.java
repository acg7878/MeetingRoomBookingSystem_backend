package com.example.meetingroombookingsystem.entity.vo.request.order;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;

@Data
public class OrderMeetingRoomBookVo {
    private String meetingRoomName;
    private String customerName;
    private Long startTime;
    private Long endTime;
}
