package com.diariasja.aws.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diariasja.aws.dto.LoginRequestDTO;
import com.diariasja.aws.entity.Usuario;
import com.diariasja.aws.exception.ResourceNotFoundException;
import com.diariasja.aws.repository.UsuarioRepository;
import com.diariasja.aws.security.TokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UsuarioRepository repository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO dto) {
        // 1. Busca o usuário no banco pelo e-mail
        Usuario usuario = repository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("Credenciais inválidas"));

        // 2. Compara a senha digitada com o Hash do banco de dados (BCrypt)
        if (passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            // 3. Se a senha bater, gera o Token JWT
            String token = tokenService.gerarToken(usuario);
            return ResponseEntity.ok(token);
        }

        // 4. Se a senha for inválida, retorna erro 401 (Não Autorizado) com mensagem genérica
        return ResponseEntity.status(401).body("Credenciais inválidas");
    }
}