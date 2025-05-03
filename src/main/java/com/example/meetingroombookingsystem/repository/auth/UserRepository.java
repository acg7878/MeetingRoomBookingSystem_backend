package com.example.meetingroombookingsystem.repository.auth;

import com.example.meetingroombookingsystem.entity.auth.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUsername(String username);
}
