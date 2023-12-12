package org.ac.cst8277.kim.riyoun.usermanagement;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.time.Instant;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Data;

@RestController
public class Controller {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private SessionRepository sessionRepo;

    @GetMapping("login/oauth2/code/github")
    public ResponseEntity<?> login(@RequestParam String code, @RequestParam String state) {
        return ResponseEntity.ok().body("Hello, world!");
    }

    @GetMapping("/user/all")
    public ResponseEntity<?> getAllUsers(@RequestParam String id) {
        return authenticate(id, user -> {
            for (Role r : user.getRoles()) {
                if (r.getRole().equals("ADMIN")) {
                    return ResponseEntity.ok().body(userRepo.findAll());
                }
            }
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Your are not authorized to do this.");
        });
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserWithId(@PathVariable("id") String id) {
        return authenticate(id, user -> {
            return ResponseEntity.ok().body(user);
        });
    }

    @GetMapping("/user")
    public ResponseEntity<?> loginUser(@RequestParam String username, @RequestParam String password) {
        Optional<User> user = userRepo.findByUsernameAndPassword(username, password);
        if (user.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("No user found. To create a new account, POST to '/usm/user' with username and password.");
        } else {
            return ResponseEntity.ok().body(user.get());
        }
    }

    @GetMapping("/role/all")
    public ResponseEntity<?> getAllRoles(@RequestParam String id) {
        return authenticate(id, user -> {
            for (Role r : user.getRoles()) {
                if (r.getRole().equals("ADMIN")) {
                    return ResponseEntity.ok().body(roleRepo.findAll());
                }
            }
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Your are not authorized to do this.");
        });
    }

    @PostMapping("/user")
    public User createUser(@RequestBody CreateUserRequest request) {
        User user = new User();
        Session session = new Session();

        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        session.setLastLogin(Timestamp.from(Instant.now()));
        session.setLastLogout(Timestamp.from(Instant.now()));
        user.setLastSession(session);

        User u = userRepo.save(user);
        sessionRepo.save(session);
        return u;
    }

    @DeleteMapping("/users/{id}")
    public void deleteById(@PathVariable("id") String id) {
        UUID uuid = UUID.fromString(id);
        userRepo.deleteById(uuid);
    }

    private ResponseEntity<?> authenticate(String id, Function<User, ResponseEntity<?>> callback) {
        try {
            UUID uuid = UUID.fromString(id);
            Optional<User> user = userRepo.findById(uuid);
            if (user.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("No user found. To create a new account, POST to '/usm/user' with username and password.");
            } else {
                return callback.apply(user.get());
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .badRequest()
                .body("Illegal UUID. To create a new account, POST to '/usm/user' with username and password.");
        }
    }
}

@Data
class CreateUserRequest {
    private String username;
    private String password;
}