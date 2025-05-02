package com.example.meetingroombookingsystem.entity.resource;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Data
@Table(name = "MeetingRooms")
public class MeetingRooms {
    public enum RoomType {CLASSROOM, ROUND_TABLE};
    public enum Status {AVAILABLE, LOCKED, BOOKED, IN_USE, UNDER_MAINTENANCE}

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int room_id;

    @Column(name = "room_name", nullable = false, unique = true)
    private String room_name;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType room_type;

    @Column(name = "seat_count", nullable = false)
    private int seat_count;

    @Column(name = "price_per_hour", nullable = false)
    private BigDecimal price_per_hour;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    // updatable = false 创建后禁止更新
    @Column(name = "created_at", nullable = false,updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp created_at;

    @ManyToMany
    @JoinTable(
            name = "MeetingRoomEquipments",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "equipment_id")
    )
    private Set<Equipments> equipments;
}
