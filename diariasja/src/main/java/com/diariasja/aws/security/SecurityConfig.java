package com.diariasja.aws.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http))
            // API REST não deve guardar estado/sessão (Stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/api/usuarios/cadastrar", 
                        "/api/categorias/**",
                        "/swagger-ui/**",       
                        "/v3/api-docs/**",      
                        "/swagger-ui.html"      
                ).permitAll()
                // A rota de diárias agora é protegida! Exige Token.
                .requestMatchers("/api/diarias/**").authenticated() 
                .anyRequest().authenticated()
            )
            // Avisa o Spring que vamos usar validação de Token JWT (via AWS Cognito)
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));
            
        return http.build();
    }
}