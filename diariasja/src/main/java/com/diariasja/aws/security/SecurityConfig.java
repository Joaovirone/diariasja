package com.diariasja.aws.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http))
            .authorizeHttpRequests(auth -> auth
                // Liberando as rotas da nossa API pública E as rotas geradas pelo Swagger
                .requestMatchers(
                        "/api/usuarios/cadastrar", 
                        "/api/categorias/**",
                        "/swagger-ui/**",       // Interface gráfica do Swagger
                        "/v3/api-docs/**",      // O JSON bruto padrão OpenAPI (usado pelo API Gateway)
                        "/swagger-ui.html"      // Redirecionamento padrão
                ).permitAll()
                .anyRequest().authenticated()
            );
            
        return http.build();
    }
}