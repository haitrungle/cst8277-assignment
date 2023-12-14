package org.ac.cst8277.kim.riyoun.usermanagement;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@RestController
public class Controller {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttributes();
    }

    @GetMapping("/user/all")
    public ResponseEntity<?> getAllUsers(@AuthenticationPrincipal OAuth2User principal) {
        if (!hasAuthority(principal, "ADMIN")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You don't have permission to do this.");
        }
        return ResponseEntity.ok().body(userRepo.findAll());
    }

    @GetMapping("/user/{name}")
    public ResponseEntity<?> getUserWithId(@PathVariable("name") String name, @AuthenticationPrincipal OAuth2User principal) {
        if (!hasAuthority(principal, "ADMIN") && !principal.getName().equals(name)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You don't have permission to do this.");
        }
        Optional<User> user = userRepo.findByUsername(name);
        if (user.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("No user found. To create a new account, POST to '/ums/user' with username and password.");
        } else {
            return ResponseEntity.ok().body(user.get());
        }
    }

    @GetMapping("/role/all")
    public ResponseEntity<?> getAllRoles(@AuthenticationPrincipal OAuth2User principal) {
        if (!hasAuthority(principal, "ADMIN")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You don't have permission to do this.");
        }
        return ResponseEntity.ok().body(roleRepo.findAll());
    }

    @PostMapping("/user")
    public User createUser(@RequestBody CreateUserRequest request) {
        User user = new User();
        Session session = new Session();

        user.setUsername(request.getUsername());
        session.setLogin(Timestamp.from(Instant.now()));
        session.setExpire(Timestamp.from(Instant.now().plus(Duration.ofMinutes(15))));
        user.setLastSession(session);

        User u = userRepo.save(user);
        return u;
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") String id, @AuthenticationPrincipal OAuth2User principal) {
        if (!hasAuthority(principal, "ADMIN")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("You don't have permission to do this.");
        }
        UUID uuid = UUID.fromString(id);
        userRepo.deleteById(uuid);
        return ResponseEntity.ok().body("Deleted user with ID " + id);
    }

    private boolean hasAuthority(OAuth2User principal, String authority) {
        System.out.println(principal.getName());
        for (GrantedAuthority r : principal.getAuthorities()) {
            System.out.println(r.getAuthority());
            if (authority.equals(r.getAuthority())) return true;
        }
        return false;
    }
}

@Data
class CreateUserRequest {
    private String username;
    private String password;
}