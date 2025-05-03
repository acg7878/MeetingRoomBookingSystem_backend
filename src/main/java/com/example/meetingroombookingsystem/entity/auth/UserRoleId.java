package com.example.meetingroombookingsystem.entity.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class UserRoleId implements Serializable {
    @Column(name = "user_id")
    private int userId;
    @Column(name = "role_id")
    private int roleId;
}
