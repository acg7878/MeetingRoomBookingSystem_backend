package com.example.meetingroombookingsystem.entity.business;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "Payments")
@Data
public class Payments {
    public enum PaymentStatus { UNPAID, PAID }
    public enum Payment_method { ALIPAY, WECHAT, BANK_CARD }

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int payment_id;

    @Column(name = "order_id", nullable = false)
    private int order_id;

    @Column(name = "payment_time", nullable = false)
    private Timestamp payment_time;

    @Column(name = "payment_amount", nullable = false)
    private BigDecimal payment_amount; // 支付金额

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private Payment_method payment_method;

    @Column(name = "created_at", nullable = false)
    private Timestamp created_at;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus payment_status;

}
