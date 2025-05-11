package com.example.meetingroombookingsystem.entity.vo.request.meetingRoom;

import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Data
public class MeetingRoomFilterVo {
    private Long startTime;
    private Long endTime;
    private Integer attendees;
    private List<String> equipment;
}
