package com.example.meetingroombookingsystem.controller;

import com.example.meetingroombookingsystem.entity.vo.RestBean;
import com.example.meetingroombookingsystem.entity.vo.response.order.CancelRequestsResponseVo;
import com.example.meetingroombookingsystem.entity.vo.response.order.OrderResponseVo;
import com.example.meetingroombookingsystem.service.OrdersService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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


    @PreAuthorize("hasAuthority('Apply for Cancel Order')")
    @GetMapping("/apply-cancel")
    public RestBean<Void> cancelOrder(@RequestParam String orderId) {
        return this.messageHandle(() ->
                orderService.applyForCancelOrder(orderId));
    }


    @PreAuthorize("hasAuthority('Pay Order')")
    @GetMapping("/pay")
    public RestBean<Void> payOrder(@RequestParam String orderId) {
        return this.messageHandle(() ->
                orderService.PayOrder(orderId));
    }

    @PreAuthorize("hasAuthority('View My Cancel Requests')")
    @GetMapping("/my-cancel-request")
    public RestBean<List<CancelRequestsResponseVo>> getCancelRequest(@RequestParam String username) {
        return RestBean.success(orderService.getCancelRequest(username));
    }

    @PreAuthorize("hasAuthority('View All Cancel Requests')")
    @GetMapping("/all-cancel-request")
    public RestBean<List<CancelRequestsResponseVo>> getAllCancelRequest() {
        return RestBean.success(orderService.getAllCancelRequest());
    }

    @PreAuthorize("hasAuthority('Review Cancel Request')")
    @PostMapping("/review-cancel-request")
    public RestBean<Void> reviewCancelRequest(@RequestBody Map<String, Object> requestBody) {
        Integer orderId = (Integer) requestBody.get("orderId");
        String status = (String) requestBody.get("status");

        return this.messageHandle(() -> orderService.reviewCancelRequest(orderId, status));
    }



    private <T> RestBean<T> messageHandle(Supplier<String> action) {
        String message = action.get();
        if (message == null)
            return RestBean.success();
        else
            return RestBean.failure(400, message);
    }
}
