package com.example.meetingroombookingsystem.entity.resource;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
@Table(name = "Equipments")
public class Equipments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int equipment_id;

    @Column(name = "equipment_name", nullable = false, unique = true)
    private String equipment_name;

    @ManyToMany(mappedBy = "equipments")
    private Set<MeetingRooms> meetingRooms;
}
