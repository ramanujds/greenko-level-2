# What is Spring Security?

Spring Security is a **security framework for authentication and authorization** in Java applications.

It handles:

* Authentication (Who are you?)
* Authorization (What can you access?)
* Password encoding
* CSRF protection
* Session management
* JWT & OAuth2
* Method-level security

Under the hood, it works using **Servlet Filters**.

---

# Internal Architecture 

When a request comes:

```
Request → Security Filter Chain → DispatcherServlet → Controller
```

The key components:

* **SecurityFilterChain**
* **AuthenticationManager**
* **AuthenticationProvider**
* **UserDetailsService**
* **PasswordEncoder**
* **SecurityContextHolder**

Flow:

1. Request hits filter
2. Credentials extracted
3. AuthenticationManager validates
4. If valid → store Authentication object in SecurityContext
5. If invalid → 401

---

# Step-by-Step: Basic Spring Security Setup

### Step 1: Add Dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Run the app.

What happens?

* All endpoints become secured.
* Default user: `user`
* Random password printed in console.

That’s auto-configuration.

---

# Step 2: Create Custom Security Configuration

Since Spring Security 6+, **WebSecurityConfigurerAdapter is removed**.

Now we define a `SecurityFilterChain` bean.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
```

This is the modern DSL approach.

---

# Step 3: In-Memory Authentication

```java
@Bean
public UserDetailsService userDetailsService() {

    UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("password"))
            .roles("USER")
            .build();

    UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin123"))
            .roles("ADMIN")
            .build();

    return new InMemoryUserDetailsManager(user, admin);
}

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Now:

* `/admin/**` → accessible only by ADMIN
* Others → require login

---

# Step 4: Using Database

Now we move to production approach.

### Entity

```java
@Entity
public class AppUser {

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String password;
    private String role;
}
```

### Repository

```java
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}
```

### Custom UserDetailsService

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    public CustomUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        AppUser user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}
```

Now authentication happens via DB.

---

# Role-Based Authorization

In config:

```java
.requestMatchers("/api/user/**").hasRole("USER")
.requestMatchers("/api/admin/**").hasRole("ADMIN")
```

Or method-level:

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/secure")
public String secureEndpoint() {
    return "Only Admin";
}
```

Enable it:

```java
@EnableMethodSecurity
```

---

# Stateless Authentication with JWT 

For microservices, we don’t use session.

We use:

* Disable session
* Add JWT filter
* Validate token on every request

### Disable Session

```java
.sessionManagement(session -> 
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```

### Flow:

1. User logs in
2. Server generates JWT
3. Client stores JWT
4. Every request sends:

   ```
   Authorization: Bearer <token>
   ```
5. Filter validates token
6. Sets authentication in SecurityContext

You add a custom filter:

```java
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(...) {
        // Extract token
        // Validate token
        // Set authentication
    }
}
```

Add filter:

```java
http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
```

This is how production systems work.

---

# CSRF – When to Disable?

* For REST APIs → Disable
* For Web Apps (Forms) → Keep enabled

```java
http.csrf(csrf -> csrf.disable());
```

---

# Spring Framework 7 Context

Spring 7 continues the same **component-based configuration model**:

* No WebSecurityConfigurerAdapter
* Lambda DSL
* Native support
* Improved observability
* Jakarta EE namespace (jakarta.*)


# 1️⃣1️⃣ Complete Minimal Production Style Config

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .build();
    }
}
```
