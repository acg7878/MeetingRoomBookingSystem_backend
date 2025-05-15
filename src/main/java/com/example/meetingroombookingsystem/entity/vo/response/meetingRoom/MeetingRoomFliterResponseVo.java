package com.example.meetingroombookingsystem.entity.vo.response.meetingRoom;

import lombok.Data;

@Data
public class MeetingRoomFliterResponseVo {
    private String roomName;
    private String roomType;
    private Double pricePerHour;
    private Integer seatCount;
    private String[] equipments;
}
