package com.diariasja.aws.controller;

import com.diariasja.aws.dto.CategoriaServicoRequestDTO;
import com.diariasja.aws.dto.CategoriaServicoResponseDTO;
import com.diariasja.aws.service.CategoriaServicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaServicoController {

    @Autowired
    private CategoriaServicoService service;

    @PostMapping
    public ResponseEntity<CategoriaServicoResponseDTO> criar(@Valid @RequestBody CategoriaServicoRequestDTO dto) {
        CategoriaServicoResponseDTO novaCategoria = service.criar(dto);
        return new ResponseEntity<>(novaCategoria, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaServicoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaServicoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<CategoriaServicoResponseDTO>> listar(
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listarComPaginacao(nome, pageable));
    }
    
}