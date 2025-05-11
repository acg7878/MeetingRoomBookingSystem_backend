package com.example.meetingroombookingsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.meetingroombookingsystem.entity.dto.Orders;
import com.example.meetingroombookingsystem.entity.dto.auth.Users;
import com.example.meetingroombookingsystem.entity.dto.meetingRoom.MeetingRooms;
import com.example.meetingroombookingsystem.entity.vo.response.OrderResponseVo;
import com.example.meetingroombookingsystem.mapper.OrderMapper;
import com.example.meetingroombookingsystem.mapper.auth.UsersMapper;
import com.example.meetingroombookingsystem.mapper.meetingRoom.MeetingRoomsMapper;
import com.example.meetingroombookingsystem.service.OrdersService;
import com.example.meetingroombookingsystem.utils.TimeUtils;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrdersService {
    @Resource
    private UsersMapper usersMapper;
    @Resource
    private MeetingRoomsMapper meetingRoomsMapper;


    @Override
    public List<OrderResponseVo> getMyOrders(String username) {
        // 1. 查询用户信息
        Users user = usersMapper.selectOne(
                new LambdaQueryWrapper<Users>().eq(Users::getUsername, username)
        );
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        // 2. 根据用户ID查询订单
        List<Orders> ordersList = this.lambdaQuery()
                .eq(Orders::getCustomerId, user.getUserId())
                .list();
        // 3. 转换订单为响应对象
        return ordersList.stream().map(order -> {
            // 查询会议室名称
            MeetingRooms meetingRoom = meetingRoomsMapper.selectById(order.getRoomId());
            OrderResponseVo vo = new OrderResponseVo();
            vo.setOrderId(order.getOrderId());
            vo.setMeetingRoomName(meetingRoom.getRoomName());
            return getOrderResponseVo(order, vo);  // ?
        }).toList();
    }

    private OrderResponseVo getOrderResponseVo(Orders order, OrderResponseVo vo) {
        vo.setStartTime(order.getStartTime());
        vo.setEndTime(order.getEndTime());
        vo.setCreateTime(order.getCreatedAt());
        vo.setUpdateTime(order.getUpdatedAt());
        vo.setTotalPrice(order.getTotalPrice());
        vo.setPaymentStatus(order.getPaymentStatus());
        return vo;
    }

    @Override
    public String cancelOrder(String orderId) {
        // 1. 查询订单是否存在
        Orders order = this.getById(orderId);
        if (order == null) {
            return "订单不存在";
        }
        // 2. 更新支付状态为“取消”
        order.setPaymentStatus("cancelled");
        order.setUpdatedAt(TimeUtils.timestampToLong(new Timestamp(System.currentTimeMillis())));
        boolean updated = this.updateById(order);
        if (!updated) {
            return "订单取消失败";
        }
        // 3. 更新会议室状态为“available”
        MeetingRooms meetingRoom = meetingRoomsMapper.selectById(order.getRoomId());
        if (meetingRoom != null) {
            meetingRoom.setStatus("available");
            meetingRoomsMapper.updateById(meetingRoom);
        }
        // 4. 返回成功信息
        return null; // null 表示成功
    }

    @Override
    public String PayOrder(String orderId) {
        // 1. 查询订单是否存在
        Orders order = this.getById(orderId);
        if (order == null) {
            return "订单不存在";
        }
        // 2. 检查订单支付状态
        if ("paid".equals(order.getPaymentStatus())) {
            return "订单已支付，无需重复支付";
        }
        // 3. 更新支付状态为“已支付”
        order.setPaymentStatus("paid");
        order.setUpdatedAt(TimeUtils.timestampToLong(new Timestamp(System.currentTimeMillis())));
        boolean updated = this.updateById(order);
        if (!updated) {
            return "订单支付失败";
        }
        // 4. 返回成功信息
        return null;
    }

    @Override
    public List<OrderResponseVo> getAllOrders() {
        // 1. 查询所有订单
        List<Orders> ordersList = this.list();
        // 2. 转换订单为响应对象
        return ordersList.stream().map(order -> {
            // 查询会议室名称
            MeetingRooms meetingRoom = meetingRoomsMapper.selectById(order.getRoomId());
            OrderResponseVo vo = new OrderResponseVo();
            vo.setOrderId(order.getOrderId());
            vo.setMeetingRoomName(meetingRoom != null ? meetingRoom.getRoomName() : "未知会议室");
            return getOrderResponseVo(order, vo);
        }).toList();
    }
}

