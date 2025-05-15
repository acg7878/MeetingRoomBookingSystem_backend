package com.example.meetingroombookingsystem.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.meetingroombookingsystem.entity.dto.order.Orders;
import com.example.meetingroombookingsystem.entity.dto.meetingRoom.MeetingRooms;
import com.example.meetingroombookingsystem.mapper.OrderMapper;
import com.example.meetingroombookingsystem.mapper.meetingRoom.MeetingRoomsMapper;
import com.example.meetingroombookingsystem.utils.TimeUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;


@Slf4j
@Component
public class OrderTimeoutListener {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private MeetingRoomsMapper meetingRoomsMapper;
    @RabbitListener(queues = "order.release.queue")
    public void handleOrderTimeout(Integer orderId) {
        log.info("监听到订单超时消息，订单ID：{}", orderId);

        Orders order = orderMapper.selectOne(
                new LambdaQueryWrapper<Orders>().eq(Orders::getOrderId, orderId)
        );

        if (order != null && "unpaid".equals(order.getPaymentStatus())) {
            // 未支付，取消订单
            order.setPaymentStatus("cancelled");
            order.setUpdatedAt(TimeUtils.timestampToLong(new Timestamp(System.currentTimeMillis())));
            orderMapper.updateById(order);
            Integer roomId = order.getRoomId();
            meetingRoomsMapper.update(null,
                    new LambdaUpdateWrapper<MeetingRooms>()
                            .eq(MeetingRooms::getRoomId, roomId)
                            .set(MeetingRooms::getStatus, "available")
            );
            log.info("订单 {} 已超时取消", orderId);
        } else {
            log.info("订单 {} 已支付或不存在，无需处理", orderId);
        }
    }
}
