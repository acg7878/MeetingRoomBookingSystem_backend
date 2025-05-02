package com.example.meetingroombookingsystem.entity.auth;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class UserRoleId implements Serializable {
    private int userId;
    private int roleId;
}
