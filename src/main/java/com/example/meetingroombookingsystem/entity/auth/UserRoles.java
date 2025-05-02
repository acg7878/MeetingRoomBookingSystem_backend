package com.example.meetingroombookingsystem.entity.auth;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "UserRoles")
@Data
public class UserRoles {

    @EmbeddedId
    private UserRoleId id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Roles role;
}
