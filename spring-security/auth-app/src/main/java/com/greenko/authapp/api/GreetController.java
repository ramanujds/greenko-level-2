package com.greenko.authapp.api;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetController {

    @GetMapping("/greet")
    public String greet() {
        return "Hello World!";
    }

    @GetMapping("/user")
    public String greetUser() {
        return "Hello User!";
    }

    @GetMapping("/admin")
    public String greetAdmin() {
        return "Hello Admin!";
    }

}
