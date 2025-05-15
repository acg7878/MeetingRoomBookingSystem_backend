package com.example.meetingroombookingsystem.entity.vo.response.order;

import lombok.Data;

@Data
public class CancelRequestsResponseVo {
    private Integer cancelRequestId; // 取消请求ID
    private String roomName;
    private String refundStatus; // 退款状态（pending, approved, rejected）
    private Double refundAmount; // 退款金额
    private Long createdAt; // 创建时间
}