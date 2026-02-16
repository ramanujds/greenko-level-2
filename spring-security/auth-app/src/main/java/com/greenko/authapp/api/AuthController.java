package com.greenko.authapp.api;

import com.greenko.authapp.dto.AuthRequestDto;
import com.greenko.authapp.service.UserDetailsServiceImpl;
import com.greenko.authapp.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {


    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserDetailsServiceImpl userDetailsServiceImpl, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }





    @PostMapping("/login")
    public String authenticate(@RequestBody AuthRequestDto authRequestDto) {

        var user = userDetailsServiceImpl.loadUserByUsername(authRequestDto.username());
        log.info("User: {}", user);

        if (passwordEncoder.matches(authRequestDto.password(), user.getPassword())) {
            String token = jwtUtil.generateTokenFromUsername(user.getUsername());
            return token;
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username/password supplied");
        }

    }


}
