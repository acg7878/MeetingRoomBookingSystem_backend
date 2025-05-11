package com.example.meetingroombookingsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.meetingroombookingsystem.entity.dto.Orders;
import com.example.meetingroombookingsystem.entity.vo.response.OrderResponseVo;

import java.util.List;

public interface OrdersService extends IService<Orders> {


    List<OrderResponseVo> getMyOrders(String username);
    String cancelOrder(String orderId);
    String PayOrder(String orderId);

    List<OrderResponseVo> getAllOrders();
}
