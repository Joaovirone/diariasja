package com.diariasja.aws.controller;

import com.diariasja.aws.entity.CategoriaServico;
import com.diariasja.aws.service.CategoriaServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Diz que os retornos serão em formato JSON
@RequestMapping("/api/categorias") // A URL base para acessar esta classe
public class CategoriaServicoController {

    @Autowired
    private CategoriaServicoService service;

    // Acessado via POST http://localhost:8080/api/categorias
    @PostMapping
    public ResponseEntity<CategoriaServico> criar(@RequestBody CategoriaServico categoria) {
        CategoriaServico novaCategoria = service.criar(categoria);
        return new ResponseEntity<>(novaCategoria, HttpStatus.CREATED); // Retorna HTTP 201
    }

    // Acessado via GET http://localhost:8080/api/categorias
    @GetMapping
    public ResponseEntity<List<CategoriaServico>> listar() {
        return ResponseEntity.ok(service.listarTodas()); // Retorna HTTP 200
    }
}