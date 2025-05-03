package com.example.meetingroombookingsystem.repository;

import com.example.meetingroombookingsystem.entity.auth.RolePermissions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionsRepository extends JpaRepository<RolePermissions, Integer> {
}
