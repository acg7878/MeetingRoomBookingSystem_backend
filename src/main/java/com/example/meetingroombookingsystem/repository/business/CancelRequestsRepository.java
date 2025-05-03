package com.example.meetingroombookingsystem.repository;

import com.example.meetingroombookingsystem.entity.business.CancelRequests;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CancelRequestsRepository extends JpaRepository<CancelRequests, Integer> {
}
