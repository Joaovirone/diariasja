package com.diariasja.aws.controller;

import com.diariasja.aws.dto.UsuarioRequestDTO;
import com.diariasja.aws.dto.UsuarioResponseDTO;
import com.diariasja.aws.entity.Usuario;
import com.diariasja.aws.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Page<UsuarioResponseDTO>> listarProfissionais(
            @PageableDefault(size = 12, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listarProfissionaisAtivos(pageable));
    }
}