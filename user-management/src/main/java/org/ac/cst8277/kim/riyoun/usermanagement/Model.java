package org.ac.cst8277.kim.riyoun.usermanagement;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.SourceType;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
class User {
    @Id
    @GeneratedValue
    private UUID id;

    @NaturalId
    @Column(nullable = false, unique = true)
    private String username;

    @CreationTimestamp(source = SourceType.DB)
    private Timestamp created;

    @ManyToMany(cascade = {
        CascadeType.PERSIST,
        CascadeType.MERGE
    })
    @JoinTable(name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<Role>();

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private Session lastSession;

    public void addRole(Role r) {
        roles.add(r);
        r.getUsers().add(this);
    }

    public void setLastSession(Session s) {
        s.setUser(this);
        lastSession = s;
    }

    @JsonIgnore
    public Map<String, Object> getAttributes() {
        return Map.of(
                "id", id,
                "username", username,
                "created", created,
                "roles", roles,
                "lastSession", lastSession);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}

@Entity
@Data
class Session {
    @Id
    @GeneratedValue
    private UUID id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "login_at")
    private Timestamp login;

    @Column(name = "expire_at")
    private Timestamp expire;

    @Override
    public String toString() {
        return "Session(id=" + id.toString() + ")";
    }

    public static Session now() {
        Session session = new Session();
        session.login = Timestamp.from(Instant.now());
        session.expire = Timestamp.from(Instant.now().plusSeconds(15 * 60));
        return session;
    }
}

@Entity
@Data
class Role implements GrantedAuthority {
    @Id
    @GeneratedValue
    private UUID id;

    @NaturalId
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<User>();

    @JsonIgnore
    @Override
    public String getAuthority() {
        return name;
    }

    @Override
    public String toString() {
        return "Role(" + name + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}