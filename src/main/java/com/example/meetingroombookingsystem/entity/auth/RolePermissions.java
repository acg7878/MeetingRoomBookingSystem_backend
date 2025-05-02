package com.example.meetingroombookingsystem.entity.auth;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "RolePermissions")
@Data
public class RolePermissions {

    @EmbeddedId
    private RolePermissionId id;

    @ManyToOne
    @MapsId("role_id")  // 映射联合主键的 role_id
    @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "fk_role_permission_role"))
    private Roles role;

    @ManyToOne
    @MapsId("permission_id")  // 映射联合主键的 permission_id
    @JoinColumn(name = "permission_id", foreignKey = @ForeignKey(name = "fk_role_permission_permission"))
    private Permissions permission;
}
