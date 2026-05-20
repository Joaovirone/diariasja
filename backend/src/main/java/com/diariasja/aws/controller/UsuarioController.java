package com.diariasja.aws.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diariasja.aws.dto.UsuarioRequestDTO;
import com.diariasja.aws.dto.UsuarioResponseDTO;
import com.diariasja.aws.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;
    @PostMapping("/cadastrar")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = service.cadastrar(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/profissionais")
    public ResponseEntity<Page<UsuarioResponseDTO>> listarPrestadores(
           @ParameterObject @PageableDefault(size = 12, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listarPrestadoresAtivos(pageable));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> buscarMeuPerfil() {
        // Pega o e-mail do token JWT que está no contexto do Spring Security
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        
        return ResponseEntity.ok(service.buscarPorEmail(email));
    }
}
