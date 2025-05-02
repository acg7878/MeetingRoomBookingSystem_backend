package com.example.meetingroombookingsystem.repository;

import com.example.meetingroombookingsystem.entity.business.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentsRepository extends JpaRepository<Payments, Integer> {
}
