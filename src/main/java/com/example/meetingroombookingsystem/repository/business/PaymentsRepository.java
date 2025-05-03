package com.example.meetingroombookingsystem.repository.business;

import com.example.meetingroombookingsystem.entity.business.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentsRepository extends JpaRepository<Payments, Integer> {
}
