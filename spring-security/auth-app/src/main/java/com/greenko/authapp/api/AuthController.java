package com.greenko.authapp.api;

import com.greenko.authapp.dto.AuthRequestDto;
import com.greenko.authapp.service.UserDetailsServiceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserDetailsServiceImpl userDetailsServiceImpl, PasswordEncoder passwordEncoder) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.passwordEncoder = passwordEncoder;
    }



    public String authenticate(@RequestBody AuthRequestDto authRequestDto) {

        var user = userDetailsServiceImpl.loadUserByUsername(authRequestDto.username());

        if (passwordEncoder.matches(authRequestDto.password(), user.getPassword())) {
            return "Authenticated";
        } else {
            return "Not Authenticated";
        }

    }


}
