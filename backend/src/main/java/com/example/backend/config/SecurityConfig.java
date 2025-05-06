package com.example.backend.config;

import com.example.backend.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/login.html", "/api/users/register", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login.html?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login.html")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .invalidSessionUrl("/login.html")
                        .maximumSessions(1)
                )
                .userDetailsService(userDetailsService);

        return http.build();
    }
}