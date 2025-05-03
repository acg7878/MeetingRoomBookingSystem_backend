package com.example.meetingroombookingsystem.repository;

import com.example.meetingroombookingsystem.entity.resource.MeetingRoomEquipments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRoomEquipmentsRepository extends JpaRepository<MeetingRoomEquipments, Integer> {
}
