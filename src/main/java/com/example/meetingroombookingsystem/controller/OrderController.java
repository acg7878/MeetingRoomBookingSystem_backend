package com.example.meetingroombookingsystem.controller;

import com.example.meetingroombookingsystem.entity.vo.RestBean;
import com.example.meetingroombookingsystem.entity.vo.response.OrderResponseVo;
import com.example.meetingroombookingsystem.service.OrdersService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    OrdersService orderService;


    @PreAuthorize("hasAuthority('View My Orders')")
    @GetMapping("/my-orders")
    public RestBean<List<OrderResponseVo>> getMyOrders(@RequestParam String username) {
        return RestBean.success(orderService.getMyOrders(username));
    }

    @PreAuthorize("hasAuthority('View All Orders')")
    @GetMapping("/all-orders")
    public RestBean<List<OrderResponseVo>> getAllOrders() {
        return RestBean.success(orderService.getAllOrders());
    }


    @PreAuthorize("hasAuthority('Cancel Order')")
    @GetMapping("/cancel")
    public RestBean<Void> cancelOrder(@RequestParam String orderId) {
        return this.messageHandle(() ->
                orderService.cancelOrder(orderId));
    }


    @PreAuthorize("hasAuthority('Pay Order')")
    @GetMapping("/pay")
    public RestBean<Void> payOrder(@RequestParam String orderId) {
        return this.messageHandle(() ->
                orderService.PayOrder(orderId));
    }

    private <T> RestBean<T> messageHandle(Supplier<String> action){
        String message = action.get();
        if(message == null)
            return RestBean.success();
        else
            return RestBean.failure(400, message);
    }
}
