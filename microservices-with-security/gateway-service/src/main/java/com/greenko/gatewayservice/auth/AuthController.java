package com.greenko.gatewayservice.auth;

import com.greenko.gatewayservice.jwt.JwtProperties;
import com.greenko.gatewayservice.jwt.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public AuthController(ReactiveAuthenticationManager authenticationManager,
                          JwtService jwtService,
                          JwtProperties jwtProperties) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public Mono<TokenResponse> login(@RequestBody LoginRequest req) {
        Authentication authRequest = new UsernamePasswordAuthenticationToken(req.username(), req.password());

        return authenticationManager.authenticate(authRequest)
                .map(authResult -> {
                    String token = jwtService.generateToken(authResult.getName(), authResult.getAuthorities().stream().toList());
                    return new TokenResponse(token, "Bearer", jwtProperties.ttlSeconds());
                });
    }
}

