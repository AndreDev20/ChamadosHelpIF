package br.edu.ifsp.chamados.config;

import br.edu.ifsp.chamados.security.JwtAuthFilter;
import br.edu.ifsp.chamados.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())

            // Sessão necessária para persistir o SecurityContext entre o redirect pós-login
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/health").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/manutencao/**").hasAnyRole("MANUTENCAO", "ADMIN")
                .requestMatchers("/incidente/**").hasAnyRole("COMUM", "ADMIN", "MANUTENCAO")
                .anyRequest().authenticated()
            )

            // Desabilita o formLogin do Spring (usamos o nosso AuthController)
            .formLogin(form -> form.disable())

            // Redireciona para login quando não autenticado (resolve o 403 → deve ser 302)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendRedirect("/auth/login"))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    response.sendRedirect("/auth/login"))
            )

            // Logout é tratado inteiramente pelo AuthController (@GetMapping("/auth/logout"))
            // O JwtAuthFilter já ignora /auth/*, então o request chega direto ao controller
            .logout(logout -> logout.disable())

            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
