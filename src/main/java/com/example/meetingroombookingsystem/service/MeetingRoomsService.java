package com.example.meetingroombookingsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.meetingroombookingsystem.entity.dto.meetingRoom.MeetingRooms;
import com.example.meetingroombookingsystem.entity.vo.request.meetingRoom.MeetingRoomCreateVo;
import com.example.meetingroombookingsystem.entity.vo.request.meetingRoom.MeetingRoomUpdateVo;
import com.example.meetingroombookingsystem.entity.vo.response.meetingRoom.MeetingRoomFliterResponseVo;
import com.example.meetingroombookingsystem.entity.vo.response.meetingRoom.MeetingRoomResponseVo;

import java.util.List;

public interface MeetingRoomsService extends IService<MeetingRooms> {
    String createMeetingRoom(MeetingRoomCreateVo meetingRoomCreateVo);
    String deleteMeetingRoom(String meetingRoomName);
    List<MeetingRoomResponseVo> listAllMeetingRooms();
    String updateMeetingRoom(MeetingRoomUpdateVo meetingRoomUpdateVo);
    String updateMeetingRoomStatus(String meetingRoomName, String status);
    String bookMeetingRoom(String meetingRoomName, String customerName, Long startTime, Long endTime);
    String updateMeetingRoomPrice(String meetingRoomName, Double pricePerHour);
    List<String> getMeetingRoomEquipment(String meetingRoomName);
    List<MeetingRoomFliterResponseVo> filterMeetingRooms(Long startTime, Long endTime, Integer attendees, List<String> equipment);
}
