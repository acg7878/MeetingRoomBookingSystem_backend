package com.example.meetingroombookingsystem.repository;

import com.example.meetingroombookingsystem.entity.auth.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionsRepository extends JpaRepository<Permissions, Integer> {
}
