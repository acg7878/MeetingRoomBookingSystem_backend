package com.example.meetingroombookingsystem.entity.auth;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "Roles")
@Data
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int role_id;

    @Column(name = "role_name", nullable = false, unique = true)
    private String role_name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "role")
    private List<UserRoles> userRoles;

}
