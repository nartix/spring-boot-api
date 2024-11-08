package com.ferozfaiz.security.user.roles;

import com.ferozfaiz.security.role.Role;
import com.ferozfaiz.security.user.User;

import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import java.util.Objects;


@Entity
@Table(name = "user_roles")
public class UserRoles implements Serializable {

    @EmbeddedId
    private UserRoleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "user_roles_user_id_fk"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "user_roles_role_id_fk"))
    @Fetch(FetchMode.JOIN)
    private Role role;

    // Getters, Setters, equals, and hashCode
    public UserRoleId getId() {
        return id;
    }

    public void setId(UserRoleId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public UserRoles() {
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoles userRoles = (UserRoles) o;
        return Objects.equals(id, userRoles.id) && Objects.equals(user, userRoles.user) && Objects.equals(role, userRoles.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, role);
    }
}