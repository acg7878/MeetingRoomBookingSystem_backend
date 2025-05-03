package com.example.meetingroombookingsystem.repository.resource;

import com.example.meetingroombookingsystem.entity.resource.MeetingRooms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRoomsRepository extends JpaRepository<MeetingRooms, Integer> {
}
