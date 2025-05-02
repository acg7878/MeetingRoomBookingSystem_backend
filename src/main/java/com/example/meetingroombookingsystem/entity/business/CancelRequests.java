package com.example.meetingroombookingsystem.entity.business;

import com.example.meetingroombookingsystem.entity.auth.Users;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "CancelRequests")
@Data
public class CancelRequests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cancel_id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cancel_request_order",
                    foreignKeyDefinition = "FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE"))
    private Orders order;

    @Column(name = "request_time", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp request_time;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status", nullable = false)
    private RefundStatus refund_status;

    @ManyToOne
    @JoinColumn(name = "approved_by", nullable = true,
            foreignKey = @ForeignKey(name = "fk_cancel_request_approved_by",
                    foreignKeyDefinition = "FOREIGN KEY (approved_by) REFERENCES Users(user_id) ON DELETE SET NULL"))
    private Users approved_by;

    @Column(name = "refund_amount", nullable = true)
    private BigDecimal refund_amount;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp created_at;

    // Enum type for refund status
    public enum RefundStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
