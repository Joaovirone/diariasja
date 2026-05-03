package com.diariasja.aws.security;

import com.diariasja.aws.entity.Usuario;
import com.diariasja.aws.repository.UsuarioRepository;
import com.diariasja.aws.security.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Transforma esta classe num componente gerido pelo Spring
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Recupera o token do cabeçalho da requisição
        var token = this.recoverToken(request);
        
        if (token != null) {
            // 2. Valida o token e extrai o e-mail do utilizador (Subject)
            var login = tokenService.validarToken(token);

            if (!login.isEmpty()) {
                // 3. Vai à base de dados procurar o utilizador por trás daquele token
                Usuario usuario = repository.findByEmail(login)
                        .orElseThrow(() -> new RuntimeException("Utilizador não encontrado no token"));

                // 4. Cria o objeto de Autenticação do Spring Security
                // (O terceiro parâmetro seria para as 'Roles'/Permissões, passamos nulo neste MVP)
                var authentication = new UsernamePasswordAuthenticationToken(usuario, null, null);

                // 5. Força o login do utilizador no contexto do Spring para esta requisição específica
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        // 6. Passa a requisição para a frente (para o próximo filtro ou para o Controller)
        filterChain.doFilter(request, response);
    }

    // Método auxiliar para extrair a palavra "Bearer " e deixar apenas o código JWT
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}