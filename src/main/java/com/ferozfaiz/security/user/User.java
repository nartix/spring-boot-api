package com.ferozfaiz.security.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ferozfaiz.common.annotation.ValidEmail;
import com.ferozfaiz.security.role.Role;
import com.ferozfaiz.security.user.roles.UserRoles;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_user_username", columnNames = "username"),
                @UniqueConstraint(name = "UK_user_email", columnNames = "email")
        },
        indexes = {
                @Index(name = "IDX_user_username", columnList = "username"),
                @Index(name = "IDX_user_email", columnList = "email")
        })
@DependsOn("entityManagerFactory")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements UserDetails {

    public static final String USER_ROLES_ROLE = "userRoles.role";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{common.validation.constraints.NotBlank.message}")
    @Size(min = 4, max = 20, message = "{common.validation.constraints.Size.message}")
    @Column(nullable = false, unique = true, length = 150)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "{common.validation.constraints.NotBlank.message}")
    @Size(min = 8, max = 128, message = "{common.validation.constraints.Size.message}")
    @Column(nullable = false, length = 128)
    private String password;

    @ValidEmail
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Size( max = 150, message = "{common.validation.constraints.Size.max.message}")
    @Column(length = 150)
    private String firstName;

    @Size( max = 150, message = "{common.validation.constraints.Size.max.message}")
    @Column(length = 150)
    private String lastName;

    @NotNull(message = "{common.validation.constraints.NotBlank.message}")
    @Column(nullable = false)
    private boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnore
    private Set<UserRoles> userRoles = new HashSet<>();

    @Column(nullable = false)
    private boolean accountNonExpired = true;

    @Column(nullable = false)
    private boolean accountNonLocked = true;

    @Column(nullable = false)
    private boolean credentialsNonExpired = true;

    @Column(nullable = false)
    private boolean enabled = true;

    public User(Long id, String username, String password, String email, String firstName, String lastName, boolean isActive, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isActive = isActive;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return getRoles().stream()
//                .flatMap(role -> role.getPermissions().stream())
//                .collect(Collectors.toSet());
//    }

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return getRoles().stream()
//                .flatMap(role -> role.getPermissions().stream())
//                .collect(Collectors.toSet());
//    }


//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(name = "roles_permissions",
//            joinColumns = @JoinColumn(name = "role_id"),
//            inverseJoinColumns = @JoinColumn(name = "permission_id"))
//    private Set<Permission> permissions = new HashSet<>();
//
//
//    public Set<Permission> getPermissions() {
//        return permissions;
//    }
//
//    public void setPermissions(Set<Permission> permissions) {
//        this.permissions = permissions;
//    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isActive == user.isActive && Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(email, user.email) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(createdAt, user.createdAt) && Objects.equals(updatedAt, user.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, email, firstName, lastName, isActive, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username='" + username + '\'' + ", password='" + password + '\'' + ", email='" + email + '\'' + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", isActive=" + isActive + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
    }

    public Set<Role> getRoles() {
        return userRoles.stream().map(UserRoles::getRole).collect(Collectors.toSet());
    }

    public User() {
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}