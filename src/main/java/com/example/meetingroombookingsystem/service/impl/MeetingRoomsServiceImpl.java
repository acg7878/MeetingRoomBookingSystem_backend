package com.example.meetingroombookingsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.meetingroombookingsystem.entity.dto.meetingRoom.MeetingRooms;
import com.example.meetingroombookingsystem.entity.vo.request.meetingRoom.MeetingRoomCreateVo;
import com.example.meetingroombookingsystem.entity.vo.request.meetingRoom.MeetingRoomUpdateVo;
import com.example.meetingroombookingsystem.entity.vo.response.meetingRoom.MeetingRoomResponseVo;
import com.example.meetingroombookingsystem.mapper.meetingRoom.MeetingRoomsMapper;
import com.example.meetingroombookingsystem.service.MeetingRoomsService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class MeetingRoomsServiceImpl extends ServiceImpl<MeetingRoomsMapper, MeetingRooms> implements MeetingRoomsService {

    public String createMeetingRoom(MeetingRoomCreateVo meetingRoomCreateVo) {
        String meetingRoomName = meetingRoomCreateVo.getRoomName();
        String roomType = meetingRoomCreateVo.getRoomType();
        Double pricePerHour = meetingRoomCreateVo.getPricePerHour();
        Integer seatCount = meetingRoomCreateVo.getSeatCount();
        String roomStatus = meetingRoomCreateVo.getStatus();
        if (existsMeetingRoomByRoomName(meetingRoomName)) {
            return "会议室已存在";
        }
        MeetingRooms meetingRoom = new MeetingRooms(null, meetingRoomName, roomType, seatCount, pricePerHour, roomStatus, new Timestamp(System.currentTimeMillis()));
        if (!this.save(meetingRoom)) {
            return "会议室创建失败";
        } else
            return "会议室创建成功";
    }

    public String deleteMeetingRoom(String meetingRoomName) {
        // 检查会议室是否存在
        if (!existsMeetingRoomByRoomName(meetingRoomName)) {
            return "会议室不存在";
        }
        // 删除会议室
        boolean isDeleted = this.remove(getMeetingRoomQueryWrapper(meetingRoomName));
        return isDeleted ? "会议室删除成功" : "会议室删除失败";
    }

    @Override
    public List<MeetingRoomResponseVo> listAllMeetingRooms() {
        return this.list().stream().map(meetingRoom -> {
            MeetingRoomResponseVo vo = new MeetingRoomResponseVo();
            vo.setRoomName(meetingRoom.getRoomName());
            vo.setRoomType(meetingRoom.getRoomType());
            vo.setPricePerHour(meetingRoom.getPricePerHour());
            vo.setSeatCount(meetingRoom.getSeatCount());
            vo.setStatus(meetingRoom.getStatus());
            return vo;
        }).toList();
    }

    @Override
    public String updateMeetingRoom(MeetingRoomUpdateVo meetingRoomUpdateVo) {
        // 查询旧的会议室
        QueryWrapper<MeetingRooms> queryWrapper = getMeetingRoomQueryWrapper(meetingRoomUpdateVo.getOldRoomName());
        MeetingRooms existingRoom = this.getOne(queryWrapper);
        if (existingRoom == null) {
            return "会议室不存在";
        }
        // 更新字段
        existingRoom.setRoomName(meetingRoomUpdateVo.getNewRoomName());
        existingRoom.setRoomType(meetingRoomUpdateVo.getRoomType());
        existingRoom.setSeatCount(meetingRoomUpdateVo.getSeatCount());
        existingRoom.setPricePerHour(meetingRoomUpdateVo.getPricePerHour());
        existingRoom.setStatus(meetingRoomUpdateVo.getStatus());
        boolean updated = this.updateById(existingRoom);
        return updated ? "会议室更新成功" : "会议室更新失败";
    }


    /**
     * 查询会议室名字是否已经存在
     *
     * @param meetingRoomName 会议室名称
     * @return 是否存在
     */
    private boolean existsMeetingRoomByRoomName(String meetingRoomName) {
        return this.baseMapper.exists(Wrappers.<MeetingRooms>query().eq("room_name", meetingRoomName));
    }

    /**
     * 根据会议室名称生成查询条件
     *
     * @param meetingRoomName 会议室名称
     * @return 查询条件
     */
    private QueryWrapper<MeetingRooms> getMeetingRoomQueryWrapper(String meetingRoomName) {
        return Wrappers.<MeetingRooms>query().eq("room_name", meetingRoomName);
    }

}
