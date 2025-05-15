package com.example.meetingroombookingsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.meetingroombookingsystem.entity.dto.order.Orders;
import com.example.meetingroombookingsystem.entity.dto.auth.Users;
import com.example.meetingroombookingsystem.entity.dto.meetingRoom.Equipments;
import com.example.meetingroombookingsystem.entity.dto.meetingRoom.MeetingRoomEquipments;
import com.example.meetingroombookingsystem.entity.dto.meetingRoom.MeetingRooms;
import com.example.meetingroombookingsystem.entity.vo.request.meetingRoom.MeetingRoomCreateVo;
import com.example.meetingroombookingsystem.entity.vo.request.meetingRoom.MeetingRoomUpdateVo;
import com.example.meetingroombookingsystem.entity.vo.response.meetingRoom.MeetingRoomFliterResponseVo;
import com.example.meetingroombookingsystem.entity.vo.response.meetingRoom.MeetingRoomResponseVo;
import com.example.meetingroombookingsystem.mapper.EquipmentsMapper;
import com.example.meetingroombookingsystem.mapper.OrderMapper;
import com.example.meetingroombookingsystem.mapper.auth.UsersMapper;
import com.example.meetingroombookingsystem.mapper.meetingRoom.MeetingRoomEquipmentMapper;
import com.example.meetingroombookingsystem.mapper.meetingRoom.MeetingRoomsMapper;
import com.example.meetingroombookingsystem.service.MeetingRoomsService;
import com.example.meetingroombookingsystem.utils.TimeUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MeetingRoomsServiceImpl extends ServiceImpl<MeetingRoomsMapper, MeetingRooms> implements MeetingRoomsService {
    @Resource
    private EquipmentsMapper equipmentsMapper;
    @Resource
    private MeetingRoomEquipmentMapper meetingRoomEquipmentMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private UsersMapper usersMapper;
    @Resource
    AmqpTemplate rabbitTemplate;

    public String createMeetingRoom(MeetingRoomCreateVo meetingRoomCreateVo) {
        String meetingRoomName = meetingRoomCreateVo.getRoomName();
        String roomType = meetingRoomCreateVo.getRoomType();
        Double pricePerHour = meetingRoomCreateVo.getPricePerHour();
        Integer seatCount = meetingRoomCreateVo.getSeatCount();
        String roomStatus = meetingRoomCreateVo.getStatus();
        if (existsMeetingRoomByRoomName(meetingRoomName)) {
            return "会议室已存在";
        }
        MeetingRooms meetingRoom = new MeetingRooms(null, meetingRoomName, roomType, seatCount, pricePerHour, roomStatus, TimeUtils.timestampToLong(new Timestamp(System.currentTimeMillis())));
        if (!this.save(meetingRoom)) {
            return "会议室创建失败";
        }
        Integer roomId = meetingRoom.getRoomId();
        String[] equipmentList = meetingRoomCreateVo.getEquipments();
        if (equipmentList != null && equipmentList.length > 0) {
            // 查询设备ID并插入到MeetingRoomEquipments表
            List<Equipments> equipments = equipmentsMapper.selectList(
                    new LambdaQueryWrapper<Equipments>().in(Equipments::getEquipmentName, Arrays.asList(equipmentList))
            );
            if (equipments.isEmpty()) {
                return "部分设备不存在";
            }
            List<MeetingRoomEquipments> meetingRoomEquipments = equipments.stream()
                    .map(equipment -> new MeetingRoomEquipments(roomId, equipment.getEquipmentId()))
                    .toList();
            for (MeetingRoomEquipments equipment : meetingRoomEquipments) {
                meetingRoomEquipmentMapper.insert(equipment);
            }
        }
        return null;
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
            // 查询设备并设置
            List<String> equipmentNames = meetingRoomEquipmentMapper.selectList(
                            new LambdaQueryWrapper<MeetingRoomEquipments>()
                                    .eq(MeetingRoomEquipments::getRoomId, meetingRoom.getRoomId())
                    ).stream().map(equipment -> equipmentsMapper.selectById(equipment.getEquipmentId()).getEquipmentName())
                    .toList();
            vo.setEquipments(equipmentNames.toArray(new String[0]));
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
        if (!updated) {
            return "会议室更新失败";
        }
        // 处理设备更新
        String[] equipmentList = meetingRoomUpdateVo.getEquipments();
        Integer roomId = existingRoom.getRoomId();
        // 删除旧的设备关联
        meetingRoomEquipmentMapper.delete(
                new LambdaQueryWrapper<MeetingRoomEquipments>().eq(MeetingRoomEquipments::getRoomId, roomId)
        );
        // 插入新的设备关联
        if (equipmentList != null && equipmentList.length > 0) {
            List<Equipments> equipments = equipmentsMapper.selectList(
                    new LambdaQueryWrapper<Equipments>().in(Equipments::getEquipmentName, Arrays.asList(equipmentList))
            );
            if (equipments.isEmpty()) {
                return "部分设备不存在";
            }
            List<MeetingRoomEquipments> meetingRoomEquipments = equipments.stream()
                    .map(equipment -> new MeetingRoomEquipments(roomId, equipment.getEquipmentId()))
                    .toList();
            for (MeetingRoomEquipments equipment : meetingRoomEquipments) {
                meetingRoomEquipmentMapper.insert(equipment);
            }
        }
        return null;
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
        return updated ? null : "会议室状态更新失败";
    }

    @Override
    public String bookMeetingRoom(String meetingRoomName, String customerName, Long startTime, Long endTime) {
        MeetingRooms meetingRoom = this.baseMapper.selectOne(
                new LambdaQueryWrapper<MeetingRooms>().eq(MeetingRooms::getRoomName, meetingRoomName)
        );
        if (meetingRoom == null) {
            return "会议室不存在";
        }
        // 2. 检查会议室状态
        if (!"available".equals(meetingRoom.getStatus())) {
            return "会议室不可用";
        }
        // 3. 查询用户 ID
        Users user = usersMapper.selectOne(
                new LambdaQueryWrapper<Users>().eq(Users::getUsername, customerName)
        );
        if (user == null) {
            return "用户不存在";
        }
        Integer customerId = user.getUserId();
        // 4. 创建订单
        Double pricePerHour = meetingRoom.getPricePerHour();
        long durationInHours = (endTime - startTime) / (1000 * 60 * 60); // 计算时长（小时）
        Double totalPrice = pricePerHour * durationInHours;
        Orders order = new Orders();
        order.setRoomId(meetingRoom.getRoomId());
        order.setCustomerId(customerId);
        order.setStartTime(startTime);
        order.setEndTime(endTime);
        order.setCreatedAt(TimeUtils.timestampToLong(new Timestamp(System.currentTimeMillis())));
        order.setUpdatedAt(TimeUtils.timestampToLong(new Timestamp(System.currentTimeMillis())));
        order.setTotalPrice(totalPrice); // 计算总价
        order.setPaymentStatus("unpaid");
        boolean saved = orderMapper.insert(order) > 0;
        if (!saved) {
            return "订单创建失败";
        }
        meetingRoom.setStatus("locked");
        this.updateById(meetingRoom);

        // 6. 发送延迟消息（60秒未支付取消）
        rabbitTemplate.convertAndSend(
                "order.event.exchange",       // 你定义的延迟交换机
                "order.delay.release",        // routing key
                order.getOrderId(),           // 消息内容
                message -> {
                    message.getMessageProperties().setExpiration("1800000"); // 30分钟
                    return message;
                }
        );
        return null;
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

    @Override
    public List<MeetingRoomFliterResponseVo> filterMeetingRooms(Long startTime, Long endTime, Integer attendees, List<String> equipment) {
        // 1. 查询已被预订的会议室 ID
        List<Integer> bookedRoomIds = orderMapper.selectList(
                new LambdaQueryWrapper<Orders>()
                        .ne(Orders::getPaymentStatus, "cancelled") // 支付状态不是取消
                        .and(wrapper -> wrapper
                                .le(Orders::getStartTime, endTime)
                                .ge(Orders::getEndTime, startTime)
                        )
        ).stream().map(Orders::getRoomId).toList();

        // 2. 查询包含所有指定设备的会议室 ID
        List<Integer> roomIdsWithAllEquipment = Collections.emptyList();
        if (equipment != null && !equipment.isEmpty()) {
            List<Integer> equipmentIds = equipmentsMapper.selectList(
                    new LambdaQueryWrapper<Equipments>()
                            .in(Equipments::getEquipmentName, equipment)
            ).stream().map(Equipments::getEquipmentId).toList();

            if (!equipmentIds.isEmpty()) {
                roomIdsWithAllEquipment = meetingRoomEquipmentMapper.selectList(
                                new LambdaQueryWrapper<MeetingRoomEquipments>()
                                        .in(MeetingRoomEquipments::getEquipmentId, equipmentIds)
                        ).stream()
                        .collect(Collectors.groupingBy(MeetingRoomEquipments::getRoomId, Collectors.mapping(MeetingRoomEquipments::getEquipmentId, Collectors.toSet())))
                        .entrySet().stream()
                        .filter(entry -> entry.getValue().containsAll(equipmentIds)) // 过滤出包含所有设备的会议室
                        .map(Map.Entry::getKey)
                        .toList();
            }
        }

        // 如果设备条件不为空但没有找到符合条件的会议室，直接返回空列表
        if (equipment != null && !equipment.isEmpty() && roomIdsWithAllEquipment.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 查询满足条件的会议室
        List<MeetingRooms> availableRooms = this.list(
                new LambdaQueryWrapper<MeetingRooms>()
                        .ge(MeetingRooms::getSeatCount, attendees) // 满足人数要求
                        .eq(MeetingRooms::getStatus, "available") // 状态为可用
                        .notIn(!bookedRoomIds.isEmpty(), MeetingRooms::getRoomId, bookedRoomIds) // 排除已被预订的会议室
                        .in(!roomIdsWithAllEquipment.isEmpty(), MeetingRooms::getRoomId, roomIdsWithAllEquipment) // 包含所有指定设备
        );

        // 4. 转换为响应对象
        return availableRooms.stream().map(room -> {
            MeetingRoomFliterResponseVo vo = new MeetingRoomFliterResponseVo();
            vo.setRoomName(room.getRoomName());
            vo.setRoomType(room.getRoomType());
            vo.setPricePerHour(room.getPricePerHour());
            vo.setSeatCount(room.getSeatCount());
            // 查询并设置设备
            List<String> equipmentNames = meetingRoomEquipmentMapper.selectList(
                            new LambdaQueryWrapper<MeetingRoomEquipments>()
                                    .eq(MeetingRoomEquipments::getRoomId, room.getRoomId())
                    ).stream().map(equipmentEntity -> equipmentsMapper.selectById(equipmentEntity.getEquipmentId()).getEquipmentName())
                    .toList();
            vo.setEquipments(equipmentNames.toArray(new String[0]));
            return vo;
        }).toList();
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
