package com.example.meetingroombookingsystem.repository.auth;

import com.example.meetingroombookingsystem.entity.auth.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<Roles, Integer> {
    Roles findByRoleId(Integer roleId);
}
