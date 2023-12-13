package org.ac.cst8277.kim.riyoun.usermanagement;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
        http
                .authorizeExchange(authz -> authz
                        .pathMatchers("/", "/error", "/webjars/**").permitAll()
                        .anyExchange().authenticated())
				.csrf(c -> c
					.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
				)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((exchange, ex) -> Mono
                                .fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))))
                .oauth2Login(Customizer.withDefaults());
        return http.build();
    }

    @Bean
	public ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return new MyUserService();
	}
}

class MyUserService extends DefaultReactiveOAuth2UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SessionRepository sessionRepository;

    public MyUserService() {
        super();
    }

    @Override
    public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Delegate the user loading process to the default implementation
        return super.loadUser(userRequest)
                .map(oauth2User -> {
                    // Retrieve necessary user information from the OAuth2User object
                    String username = oauth2User.getAttribute("login");
                    Optional<User> result = userRepository.findByUsername(username);
                    if (result.isEmpty()) {
                        // Save the user information to the database
                        return saveUserToDatabase(oauth2User.getAttributes());
                    } else {
                        User user = result.get();
                        return user;
                    }
                });
    }

    private User saveUserToDatabase(Map<String, Object> attributes) {
        // Implement the logic to save user information to the database using the UserRepository
        // You can create a User entity and save it to the UserRepository
        User user = new User();
        Role role = roleRepository.findByName("ADMIN").get();
        Session session = new Session();

        user.setUsername(attributes.get("login").toString());
        user.setPassword("");
        user.setRoles(Collections.emptyList());
        role.getUsers().add(user);
        user.setLastSession(session);
        session.setUser(user);
        session.setLastLogin(Timestamp.from(Instant.now()));

        User u = userRepository.save(user);
        sessionRepository.save(session);
        return u;
    }
}
