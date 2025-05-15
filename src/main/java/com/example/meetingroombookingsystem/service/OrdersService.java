package com.example.meetingroombookingsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.meetingroombookingsystem.entity.dto.order.Orders;
import com.example.meetingroombookingsystem.entity.vo.response.order.CancelRequestsResponseVo;
import com.example.meetingroombookingsystem.entity.vo.response.order.OrderResponseVo;

import java.util.List;

public interface OrdersService extends IService<Orders> {


    List<OrderResponseVo> getMyOrders(String username);
    String cancelOrder(String orderId);
    String PayOrder(String orderId);

    List<OrderResponseVo> getAllOrders();

    String applyForCancelOrder(String orderId);


    List<CancelRequestsResponseVo> getCancelRequest(String username);

    List<CancelRequestsResponseVo> getAllCancelRequest();


    String reviewCancelRequest(Integer orderId, String status);
}
