package org.ac.cst8277.kim.riyoun.usermanagement;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.session.ReactiveMapSessionRepository;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableSpringWebSession
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
        http
                .authorizeExchange(authz -> authz
                        .pathMatchers("/", "/error", "/webjars/**").permitAll()
                        .anyExchange().authenticated())
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

    @Bean
    public ReactiveSessionRepository<org.springframework.session.MapSession> sessionRepositoryCustomizer() {
        return new CustomReactiveSessionRepository<org.springframework.session.MapSession>(
                new ReactiveMapSessionRepository(new ConcurrentHashMap<>()), 1 * 60);
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
                    String username = oauth2User.getAttribute("login");
                    Optional<User> result = userRepository.findByUsername(username);
                    User user;
                    if (result.isEmpty()) {
                        user = saveUserToDatabase(oauth2User.getAttributes());
                    } else {
                        user = result.get();
                    }
                    return new DefaultOAuth2User(user.getRoles(), user.getAttributes(), "username");
                });
    }

    @Transactional
    private User saveUserToDatabase(Map<String, Object> attributes) {
        User user = new User();
        Role role = roleRepository.findByName("ADMIN").get();
        Session session = Session.now();

        user.setUsername(attributes.get("login").toString());
        user.setLastSession(session);

        userRepository.save(user);
        sessionRepository.save(session);

        user.addRole(role);
        return userRepository.save(user);
    }
}

class CustomReactiveSessionRepository<S extends org.springframework.session.Session>
        implements ReactiveSessionRepository<S> {
    private final ReactiveSessionRepository<S> delegate;
    private final int maxInactiveIntervalInSeconds;

    public CustomReactiveSessionRepository(ReactiveSessionRepository<S> delegate, int maxInactiveIntervalInSeconds) {
        this.delegate = delegate;
        this.maxInactiveIntervalInSeconds = maxInactiveIntervalInSeconds;
    }

    @Override
    public Mono<S> createSession() {
        return delegate.createSession().map(this::applyMaxInactiveInterval);
    }

    @Override
    public Mono<Void> save(S session) {
        return delegate.save(session).then();
    }

    @Override
    public Mono<S> findById(String id) {
        return delegate.findById(id).map(this::applyMaxInactiveInterval);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return delegate.deleteById(id);
    }

    private S applyMaxInactiveInterval(S session) {
        session.setMaxInactiveInterval(Duration.ofSeconds(maxInactiveIntervalInSeconds));
        return session;
    }
}
