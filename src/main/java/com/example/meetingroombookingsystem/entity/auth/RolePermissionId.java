package com.example.meetingroombookingsystem.entity.auth;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class RolePermissionId implements Serializable {
    private int role_id;
    private int permission_id;
}
