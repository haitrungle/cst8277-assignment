package org.ac.cst8277.kim.riyoun.usermanagement;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


// Spring Data JPA creates CRUD implementation at runtime automatically.
interface UserRepository extends JpaRepository<User, UUID> {
    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN FETCH u.roles " + 
            "JOIN FETCH u.lastSession")
    List<User> findAll();

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN FETCH u.roles " + 
            "JOIN FETCH u.lastSession WHERE u.id = :id")
    Optional<User> findById(@Param("id") UUID id);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN FETCH u.roles " + 
            "JOIN FETCH u.lastSession WHERE u.username = :username AND u.password = :password")
    Optional<User> findByUsernameAndPassword(@Param("username") String username, @Param("username") String password);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN FETCH u.roles " + 
            "JOIN FETCH u.lastSession WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);
}

interface RoleRepository extends JpaRepository<Role, UUID> {
    @Query("SELECT DISTINCT r FROM Role r WHERE r.role = :role")
    Optional<Role> findByName(@Param("role") String role);
}

interface SessionRepository extends JpaRepository<Session, UUID> {
    @Query("SELECT DISTINCT s FROM Session s " +
            "JOIN FETCH s.user WHERE s.id = :id")
    Optional<Session> findById(@Param("id") UUID id);
}