package org.ac.cst8277.kim.riyoun.usermanagement;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
class User implements OAuth2User {
    @Id
    @GeneratedValue
    private UUID id;

    private String username;

    private String password;

    @CreationTimestamp(source = SourceType.DB)
    private Timestamp created;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private Session lastSession;

    @Override
    public Map<String, Object> getAttributes() {
        System.out.println("id: " + id);
        System.out.println("username: " + username);
        System.out.println("password: " + password);
        System.out.println("created: " + created);
        System.out.println("roles: " + roles);
        System.out.println("lastSession: " + lastSession);
        System.out.println("----------");
        return Map.of(
                "id", id,
                "username", username,
                "password", password,
                "created", created,
                "roles", roles,
                "lastSession", lastSession);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getName() {
        return username;
    }
}

@Entity
@Data
@JsonIgnoreProperties(value = { "user" })
class Session {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "last_login")
    private Timestamp lastLogin;

    @Column(name = "last_logout")
    private Timestamp lastLogout;

    @Override
    public String toString() {
        return "Session(id=" + id.toString() + ")";
    }
}

@Entity
@Data
@JsonIgnoreProperties(value = { "users" }) // avoid cyclic reference
class Role implements GrantedAuthority {
    @Id
    @GeneratedValue
    private UUID id;

    private String role;

    private String description;

    @ManyToMany(mappedBy = "roles")
    private List<User> users;

    @Override
    public String getAuthority() {
        return "Role(role=" + role + ")";
    }

    @Override
    public String toString() {
        return role;
    }
}