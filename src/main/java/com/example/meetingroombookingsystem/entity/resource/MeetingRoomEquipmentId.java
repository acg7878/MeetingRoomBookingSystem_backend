package com.example.meetingroombookingsystem.entity.resource;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class MeetingRoomEquipmentId implements Serializable {
    private int room_id;
    private int equipment_id;
}
