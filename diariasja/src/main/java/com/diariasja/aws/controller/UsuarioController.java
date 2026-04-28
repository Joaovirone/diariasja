package com.diariasja.aws.controller;

import com.diariasja.aws.entity.Usuario;
import com.diariasja.aws.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PostMapping("/cadastrar")
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
        // Nota Senior: Em produção, nunca recebemos e devolvemos a Entidade diretamente.
        // Usamos DTOs (Data Transfer Objects) para não vazar a senha no JSON de resposta.
        Usuario novoUsuario = service.cadastrar(usuario);
        return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
    }
}