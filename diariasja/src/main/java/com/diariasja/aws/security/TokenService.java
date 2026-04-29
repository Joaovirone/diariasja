package com.diariasja.aws.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.diariasja.aws.entity.Usuario;

@Service
public class TokenService {

    // Lê a chave secreta do seu application.yml (ou .env)
    @Value("${api.security.token.secret:chave-secreta-padrao-mvp}")
    private String secret;

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("api-diarias-ja")
                    .withSubject(usuario.getEmail()) // O e-mail é a nossa identificação única
                    .withClaim("id", usuario.getId()) // Guardamos o ID dentro do token para facilitar!
                    .withClaim("tipo", usuario.getTipo().name())
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algoritmo);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String validarToken(String token) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer("api-diarias-ja")
                    .build()
                    .verify(token)
                    .getSubject(); // Devolve o e-mail se o token for válido e não estiver expirado
        } catch (JWTVerificationException exception) {
            return ""; // Se o token for falso ou expirado, devolve vazio e o Spring bloqueia a requisição
        }
    }

    private Instant gerarDataExpiracao() {
        // Token expira em 2 horas
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}