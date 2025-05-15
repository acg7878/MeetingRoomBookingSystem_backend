package com.example.meetingroombookingsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.meetingroombookingsystem.entity.dto.order.CancelRequests;
import com.example.meetingroombookingsystem.entity.dto.order.Orders;
import com.example.meetingroombookingsystem.entity.dto.auth.Users;
import com.example.meetingroombookingsystem.entity.dto.meetingRoom.MeetingRooms;
import com.example.meetingroombookingsystem.entity.vo.response.order.CancelRequestsResponseVo;
import com.example.meetingroombookingsystem.entity.vo.response.order.OrderResponseVo;
import com.example.meetingroombookingsystem.mapper.CancelRequestMapper;
import com.example.meetingroombookingsystem.mapper.OrderMapper;
import com.example.meetingroombookingsystem.mapper.auth.UsersMapper;
import com.example.meetingroombookingsystem.mapper.meetingRoom.MeetingRoomsMapper;
import com.example.meetingroombookingsystem.service.OrdersService;
import com.example.meetingroombookingsystem.utils.TimeUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrdersService {
    @Resource
    private UsersMapper usersMapper;
    @Resource
    private MeetingRoomsMapper meetingRoomsMapper;
    @Resource
    CancelRequestMapper cancelRequestsMapper;

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

    @Override
    public String applyForCancelOrder(String orderId) {
        Orders order = this.getById(orderId);
        if (order == null) {
            return "订单不存在";
        }
        // 2. 检查订单当前状态
        if ("cancelled".equals(order.getPaymentStatus())) {
            return "订单已取消，无法再次申请取消";
        }
        if ("apply_cancel".equals(order.getPaymentStatus())) {
            return "订单已申请取消，请勿重复操作";
        }

        // 3. 计算退款金额
        long currentTime = System.currentTimeMillis();
        long startTime = order.getStartTime();
        long hoursDifference = (startTime - currentTime) / (1000 * 60 * 60); // 转换为小时
        double refundAmount = 0.0;

        if (hoursDifference >= 72) {
            refundAmount = order.getTotalPrice(); // 全额退款
        } else if (hoursDifference >= 48) {
            refundAmount = order.getTotalPrice() * 0.75; // 退 75%
        } else if (hoursDifference >= 24) {
            refundAmount = order.getTotalPrice() * 0.25; // 退 25%
        } else {
            refundAmount = 0.0; // 不退款
        }

        // 4. 更新订单状态
        order.setPaymentStatus("apply_cancel");
        order.setUpdatedAt(TimeUtils.timestampToLong(new Timestamp(currentTime)));
        boolean updated = this.updateById(order);
        if (!updated) {
            return "申请取消失败，请稍后重试";
        }

        // 5. 创建取消申请记录
        CancelRequests cancelRequest = new CancelRequests();
        cancelRequest.setOrderId(Integer.parseInt(orderId));
        cancelRequest.setCreatedAt(TimeUtils.timestampToLong(new Timestamp(currentTime)));
        cancelRequest.setRefundStatus("pending");
        cancelRequest.setRefundAmount(refundAmount); // 设置退款金额
        boolean inserted = cancelRequestsMapper.insert(cancelRequest) > 0;
        if (!inserted) {
            return "取消申请记录插入失败，请稍后重试";
        }

        return null;
    }

    @Override
    public List<CancelRequestsResponseVo> getCancelRequest(String username) {
        // 1. 查询用户信息
        Users user = usersMapper.selectOne(
                new LambdaQueryWrapper<Users>().eq(Users::getUsername, username)
        );
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 2. 根据用户ID查询订单记录
        List<Orders> ordersList = this.lambdaQuery()
                .eq(Orders::getCustomerId, user.getUserId())
                .list();
        if (ordersList.isEmpty()) {
            throw new IllegalArgumentException("该用户没有订单记录");
        }

        // 3. 获取所有订单的 orderId 和 roomId
        List<Integer> orderIds = ordersList.stream()
                .map(Orders::getOrderId)
                .toList();

        // 4. 根据 orderId 查询取消申请记录
        List<CancelRequests> cancelRequestsList = cancelRequestsMapper.selectList(
                new LambdaQueryWrapper<CancelRequests>().in(CancelRequests::getOrderId, orderIds)
        );

        // 5. 转换为响应对象
        return cancelRequestsList.stream().map(request -> {
            // 获取对应的订单
            Orders order = ordersList.stream()
                    .filter(o -> o.getOrderId().equals(request.getOrderId()))
                    .findFirst()
                    .orElse(null);

            // 查询会议室名称
            String meetingRoomName = null;
            if (order != null) {
                MeetingRooms meetingRoom = meetingRoomsMapper.selectById(order.getRoomId());
                meetingRoomName = meetingRoom != null ? meetingRoom.getRoomName() : "未知会议室";
            }

            // 构建响应对象
            CancelRequestsResponseVo responseVo = new CancelRequestsResponseVo();
            responseVo.setCancelRequestId(request.getCancelId());
            responseVo.setRefundStatus(request.getRefundStatus());
            responseVo.setRefundAmount(request.getRefundAmount() != null ? request.getRefundAmount() : null);
            responseVo.setCreatedAt(request.getCreatedAt());
            responseVo.setRoomName(meetingRoomName); // 添加会议室名称
            return responseVo;
        }).toList();
    }

    @Override
    public List<CancelRequestsResponseVo> getAllCancelRequest() {
        // 1. 查询所有取消申请记录
        List<CancelRequests> cancelRequestsList = cancelRequestsMapper.selectList(null);

        // 2. 获取所有订单ID
        List<Integer> orderIds = cancelRequestsList.stream()
                .map(CancelRequests::getOrderId)
                .toList();

        // 3. 查询所有相关订单
        List<Orders> ordersList = this.lambdaQuery()
                .in(Orders::getOrderId, orderIds)
                .list();

        // 4. 转换为响应对象
        return cancelRequestsList.stream().map(request -> {
            // 获取对应的订单
            Orders order = ordersList.stream()
                    .filter(o -> o.getOrderId().equals(request.getOrderId()))
                    .findFirst()
                    .orElse(null);

            // 查询会议室名称
            String meetingRoomName = null;
            if (order != null) {
                MeetingRooms meetingRoom = meetingRoomsMapper.selectById(order.getRoomId());
                meetingRoomName = meetingRoom != null ? meetingRoom.getRoomName() : "未知会议室";
            }

            // 构建响应对象
            CancelRequestsResponseVo responseVo = new CancelRequestsResponseVo();
            responseVo.setCancelRequestId(request.getCancelId());
            responseVo.setRefundStatus(request.getRefundStatus());
            responseVo.setRefundAmount(request.getRefundAmount() != null ? request.getRefundAmount() : null);
            responseVo.setCreatedAt(request.getCreatedAt());
            responseVo.setRoomName(meetingRoomName); // 添加会议室名称
            return responseVo;
        }).toList();
    }

    @Override
    public String reviewCancelRequest(Integer orderId, String status) {
        // 1. 检查订单是否存在
        Orders order = this.getById(orderId);
        if (order == null) {
            return "订单不存在";
        }

        // 2. 检查取消申请是否存在
        CancelRequests cancelRequest = cancelRequestsMapper.selectOne(
                new LambdaQueryWrapper<CancelRequests>().eq(CancelRequests::getOrderId, orderId)
        );
        if (cancelRequest == null) {
            return "取消申请不存在";
        }

        // 3. 检查状态是否合法
        if (!"approved".equals(status) && !"rejected".equals(status)) {
            return "无效的状态值";
        }

        // 4. 更新取消申请状态
        cancelRequest.setRefundStatus(status);
        //cancelRequest.setUpdatedAt(TimeUtils.timestampToLong(new Timestamp(System.currentTimeMillis())));
        boolean updatedCancelRequest = cancelRequestsMapper.updateById(cancelRequest) > 0;
        if (!updatedCancelRequest) {
            return "更新取消申请状态失败，请稍后重试";
        }

        // 5. 如果审核通过，更新订单状态为“已取消”
        if ("approved".equals(status)) {
            order.setPaymentStatus("cancelled");
            order.setUpdatedAt(TimeUtils.timestampToLong(new Timestamp(System.currentTimeMillis())));
            boolean updatedOrder = this.updateById(order);
            if (!updatedOrder) {
                return "更新订单状态失败，请稍后重试";
            }
            MeetingRooms meetingRoom = meetingRoomsMapper.selectById(order.getRoomId());
            if (meetingRoom != null) {
                meetingRoom.setStatus("available");
                boolean updatedRoom = meetingRoomsMapper.updateById(meetingRoom) > 0;
                if (!updatedRoom) {
                    return "更新会议室状态失败，请稍后重试";
                }
            }
        } else {
            // 如果审核拒绝，更新订单状态为“已拒绝”
            order.setPaymentStatus("rejected");
            order.setUpdatedAt(TimeUtils.timestampToLong(new Timestamp(System.currentTimeMillis())));
            boolean updatedOrder = this.updateById(order);
            if (!updatedOrder) {
                return "更新订单状态失败，请稍后重试";
            }
        }

        // 6. 返回成功信息
        return null; // null 表示成功
    }


}

