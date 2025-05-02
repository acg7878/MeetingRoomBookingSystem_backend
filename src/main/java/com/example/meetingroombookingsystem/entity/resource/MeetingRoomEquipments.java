package com.example.meetingroombookingsystem.entity.resource;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "MeetingRoomEquipments")
@Data
public class MeetingRoomEquipments {

    @EmbeddedId
    private MeetingRoomEquipmentId id;

    @ManyToOne
    @MapsId("room_id")  // 映射联合主键的 room_id
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(name = "fk_room_equipment_room"))
    private MeetingRooms room;

    @ManyToOne
    @MapsId("equipment_id")  // 映射联合主键的 equipment_id
    @JoinColumn(name = "equipment_id", foreignKey = @ForeignKey(name = "fk_room_equipment_equipment"))
    private Equipments equipment;
}
