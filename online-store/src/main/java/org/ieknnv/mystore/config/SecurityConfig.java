package org.ieknnv.mystore.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.session.InMemoryReactiveSessionRegistry;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.InvalidateLeastUsedServerMaximumSessionsExceededHandler;
import org.springframework.security.web.server.authentication.ServerMaximumSessionsExceededHandler;
import org.springframework.security.web.server.authentication.SessionLimit;
import org.springframework.web.server.WebSession;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ServerMaximumSessionsExceededHandler maximumSessionsExceededHandler) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers("/login", "/main/items/**", "/images/**", "/items/**").permitAll()
                        .pathMatchers("/admin/**").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .sessionManagement((sessions) -> sessions
                        .concurrentSessions((css) -> css
                                .maximumSessions(SessionLimit.of(1))
                                .maximumSessionsExceededHandler(maximumSessionsExceededHandler)
                                .sessionRegistry(new InMemoryReactiveSessionRegistry())
                        ))
                .formLogin(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutSuccessHandler((exchange, authentication) ->
                                exchange.getExchange().getSession()
                                        .flatMap(WebSession::invalidate)
                                        .then(Mono.fromRunnable(() -> {
                                            exchange.getExchange().getResponse()
                                                    .setStatusCode(HttpStatus.OK);
                                        }))))
                .anonymous(Customizer.withDefaults())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(
            ReactiveUserDetailsService userDetailsService,
            PasswordEncoder encoder) {

        var authManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authManager.setPasswordEncoder(encoder);
        return authManager;
    }

    @Bean
    ServerMaximumSessionsExceededHandler maximumSessionsExceededHandler(
            @Qualifier(WebHttpHandlerBuilder.WEB_SESSION_MANAGER_BEAN_NAME) WebSessionManager webSessionManager) {
        return new InvalidateLeastUsedServerMaximumSessionsExceededHandler(
                ((DefaultWebSessionManager) webSessionManager).getSessionStore());
    }
}
