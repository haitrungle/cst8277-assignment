package org.ac.cst8277.kim.riyoun.usermanagement;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
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
}
