package com.greenko.authapp.service;

import com.greenko.authapp.repository.AppUserRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;


    public UserDetailsServiceImpl(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        var user = appUserRepository.findByUsername(username).orElseThrow(
                    () -> new UsernameNotFoundException("User not found with username: " + username));

        return new UserDetailsImpl(user.getUsername(), user.getPassword(), user.getRole());
    }


}

class UserDetailsImpl implements UserDetails {

    private final String username;
    private final String password;
    private String role;

    public UserDetailsImpl(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+role.toUpperCase()));
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
