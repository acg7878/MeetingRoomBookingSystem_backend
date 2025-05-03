package com.example.meetingroombookingsystem.repository;

import com.example.meetingroombookingsystem.entity.auth.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;



public interface UserRolesRepository extends JpaRepository<UserRoles, Integer> {
    UserRoles findByUser_userId(Integer userId);
}