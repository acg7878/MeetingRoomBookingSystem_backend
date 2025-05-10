package com.example.meetingroombookingsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.meetingroombookingsystem.entity.dto.meetingRoom.Equipments;
import com.example.meetingroombookingsystem.entity.dto.meetingRoom.MeetingRoomEquipments;
import com.example.meetingroombookingsystem.entity.dto.meetingRoom.MeetingRooms;
import com.example.meetingroombookingsystem.entity.vo.request.meetingRoom.MeetingRoomCreateVo;
import com.example.meetingroombookingsystem.entity.vo.request.meetingRoom.MeetingRoomUpdateVo;
import com.example.meetingroombookingsystem.entity.vo.response.meetingRoom.MeetingRoomResponseVo;
import com.example.meetingroombookingsystem.mapper.meetingRoom.EquipmentsMapper;
import com.example.meetingroombookingsystem.mapper.meetingRoom.MeetingRoomEquipmentMapper;
import com.example.meetingroombookingsystem.mapper.meetingRoom.MeetingRoomsMapper;
import com.example.meetingroombookingsystem.service.MeetingRoomsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeetingRoomsServiceImpl extends ServiceImpl<MeetingRoomsMapper, MeetingRooms> implements MeetingRoomsService {
    @Resource
    private EquipmentsMapper equipmentsMapper;
    @Resource
    private MeetingRoomEquipmentMapper meetingRoomEquipmentMapper;

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
        QueryWrapper<MeetingRooms> queryWrapper = getMeetingRoomQueryWrapper(meetingRoomName);
        MeetingRooms existingRoom = this.getOne(queryWrapper);
        if (existingRoom == null) {
            return "会议室不存在";
        }
        // 删除会议室
        this.remove(queryWrapper);
        return null;
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

    @Override
    public String updateMeetingRoomStatus(String meetingRoomName, String status) {
        // 检查会议室是否存在
        QueryWrapper<MeetingRooms> queryWrapper = getMeetingRoomQueryWrapper(meetingRoomName);
        MeetingRooms existingRoom = this.getOne(queryWrapper);
        if (existingRoom == null) {
            return "会议室不存在";
        }
        // 更新会议室状态
        existingRoom.setStatus(status);
        boolean updated = this.updateById(existingRoom);
        return updated ? "会议室状态更新成功" : "会议室状态更新失败";
    }

    @Override
    public String bookMeetingRoom(String meetingRoomName, String bookingTime) {
        // 检查会议室是否存在
        QueryWrapper<MeetingRooms> queryWrapper = getMeetingRoomQueryWrapper(meetingRoomName);
        MeetingRooms existingRoom = this.getOne(queryWrapper);
        if (existingRoom == null) {
            return "会议室不存在";
        }
        // 检查会议室状态是否可预订
        if (!"available".equals(existingRoom.getStatus())) {
            return "会议室当前不可预订";
        }
        // 更新会议室状态为已预订
        existingRoom.setStatus("booked");
        boolean updated = this.updateById(existingRoom);
        return updated ? "会议室预订成功" : "会议室预订失败";
    }

    @Override
    public String cancelMeetingRoomBook(String meetingRoomName) {
        // 检查会议室是否存在
        QueryWrapper<MeetingRooms> queryWrapper = getMeetingRoomQueryWrapper(meetingRoomName);
        MeetingRooms existingRoom = this.getOne(queryWrapper);
        if (existingRoom == null) {
            return "会议室不存在";
        }
        // 检查会议室状态是否为已预订
        if (!"booked".equals(existingRoom.getStatus())) {
            return "会议室当前未被预订，无法取消";
        }
        // 更新会议室状态为可用
        existingRoom.setStatus("available");
        boolean updated = this.updateById(existingRoom);
        return updated ? "会议室预订取消成功" : "会议室预订取消失败";
    }


    @Override
    public String updateMeetingRoomPrice(String meetingRoomName, Double pricePerHour) {
        // 检查会议室是否存在
        QueryWrapper<MeetingRooms> queryWrapper = getMeetingRoomQueryWrapper(meetingRoomName);
        MeetingRooms existingRoom = this.getOne(queryWrapper);
        if (existingRoom == null) {
            return "会议室不存在";
        }
        // 更新会议室价格
        existingRoom.setPricePerHour(pricePerHour);
        boolean updated = this.updateById(existingRoom);
        return updated ? "会议室价格更新成功" : "会议室价格更新失败";
    }

    @Override
    public List<String> getMeetingRoomEquipment(String meetingRoomName) {
        // 查 roomId
        MeetingRooms room = this.baseMapper.selectOne(
                new LambdaQueryWrapper<MeetingRooms>().eq(MeetingRooms::getRoomName, meetingRoomName)
        );
        if (room == null) return Collections.emptyList();
        // 查 equipmentIds
        List<Integer> equipmentIds = meetingRoomEquipmentMapper.selectList(
                new LambdaQueryWrapper<MeetingRoomEquipments>().eq(MeetingRoomEquipments::getRoomId, room.getRoomId())
        ).stream().map(MeetingRoomEquipments::getEquipmentId).toList();
        if (equipmentIds.isEmpty()) return Collections.emptyList();
        // 查 equipmentName
        return equipmentsMapper.selectList(
                new LambdaQueryWrapper<Equipments>().in(Equipments::getEquipmentId, equipmentIds)
        ).stream().map(Equipments::getEquipmentName).collect(Collectors.toList());
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
