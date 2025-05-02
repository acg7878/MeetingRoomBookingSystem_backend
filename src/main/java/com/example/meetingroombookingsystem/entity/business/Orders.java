package com.example.meetingroombookingsystem.entity.business;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "Orders")
@Data
public class Orders {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int order_id;

    @Column(name = "customer_id", nullable = false)
    private int customer_id;

    @Column(name = "room_id", nullable = false)
    private int room_id;

    @Column(name = "start_time", nullable = false)
    private Timestamp start_time;

    @Column(name = "end_time", nullable = false)
    private Timestamp end_time;

    @Column(name = "total_price", nullable = false)
    private BigDecimal total_price;

    @Column(name = "created_at", nullable = false)
    private Timestamp created_at;

    @Column(name = "updated_at", nullable = false)
    private Timestamp updated_at;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    public enum PaymentStatus { UNPAID, PAID }
    public enum OrderStatus { PENDING, PAID, CANCELLED }


}
