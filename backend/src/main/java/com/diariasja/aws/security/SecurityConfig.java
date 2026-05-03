package com.diariasja.aws.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Injetamos o filtro que acabámos de criar
    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // --- ROTAS PÚBLICAS (Qualquer pessoa pode aceder) ---
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/cadastrar").permitAll()
                
                // Opcional: A vitrine de profissionais deve ser pública para os clientes verem quem está disponível
                .requestMatchers(HttpMethod.GET, "/api/usuarios/profissionais").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll()
                
                // Rotas do Swagger (Documentação)
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                
                // --- ROTAS PRIVADAS (Exigem o Token JWT) ---
                // Qualquer chamada para /api/diarias agora será bloqueada se não tiver o token
                .requestMatchers("/api/diarias/**").authenticated()
                
                // Qualquer outra rota que não foi mencionada acima, tranca!
                .anyRequest().authenticated() 
            )
            // Ordem: "Spring, roda o meu filtro do JWT ANTES do teu filtro de login padrão"
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}