package com.greenko.gatewayservice.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;

/**
 * Wires a simple AuthenticationManager for /auth/login.
 *
 * Spring Security will use the ReactiveUserDetailsService coming from InMemoryUserConfig.
 */
@Configuration
public class ReactiveAuthConfig {

    @Bean
    ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService) {
        return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    }
}

