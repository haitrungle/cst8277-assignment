package org.ac.cst8277.kim.riyoun.usermanagement;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Entity
@Data
class User {
  @Id
  @GeneratedValue
  private UUID id;

  private String username;

  private String password;

  @CreationTimestamp(source=SourceType.DB)
  private Timestamp created;

  @ManyToMany
  @JoinTable(
    name = "user_role",
    joinColumns = @JoinColumn(name="user_id", referencedColumnName="id"),
    inverseJoinColumns = @JoinColumn(name="role_id", referencedColumnName="id")
  )
  private List<Role> roles;

  @OneToOne(cascade=CascadeType.ALL)
  @JoinColumn(name="last_session_id", referencedColumnName="id")
  private Session lastSession;
}

@Entity
@Data
class Session {
  @Id
  @GeneratedValue
  private UUID id;

  @Column(name="last_login")
  private Timestamp lastLogin;

  @Column(name="last_logout")
  private Timestamp lastLogout;
}

@Entity
@Data
@JsonIgnoreProperties(value={"users"}) // avoid cyclic reference
class Role {
  @Id
  @GeneratedValue
  private UUID id;

  private String role;

  private String description;

  @ManyToMany(mappedBy="roles")
  private List<User> users;
}