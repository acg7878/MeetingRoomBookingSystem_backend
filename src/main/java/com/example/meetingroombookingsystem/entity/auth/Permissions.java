package com.example.meetingroombookingsystem.entity.auth;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Permissions")
@Data
public class Permissions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int permission_id;

    @Column(name = "permission_name", nullable = false, unique = true)
    private String permission_name;

    @Column(name = "description")
    private String description;
}
