package com.diariasja.aws.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.diariasja.aws.dto.DiariaRequestDTO;
import com.diariasja.aws.dto.DiariaResponseDTO; // Importante: trazer o DTO de resposta
import com.diariasja.aws.service.DiariaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/diarias")
public class DiariaController {

    @Autowired
    private DiariaService service;

    @PostMapping("/solicitar")
    // CORREÇÃO 1: Alterar ResponseEntity<Diaria> para ResponseEntity<DiariaResponseDTO>
    public ResponseEntity<DiariaResponseDTO> solicitar(@Valid @RequestBody DiariaRequestDTO dto) {
        
        // CORREÇÃO 2: A variável que recebe o resultado do service tem de ser do tipo DTO
        DiariaResponseDTO novaDiaria = service.solicitarServico(dto);
        
        return new ResponseEntity<>(novaDiaria, HttpStatus.CREATED);
    }

    // Aproveitando, aqui estão os endpoints para as regras de negócio que implementamos no Service
    @PatchMapping("/{id}/aceitar")
    public ResponseEntity<DiariaResponseDTO> aceitar(
            @PathVariable Long id, 
            @RequestParam Long idProfissional) {
        DiariaResponseDTO atualizada = service.aceitarDiaria(id, idProfissional);
        return ResponseEntity.ok(atualizada);
    }

    @PatchMapping("/{id}/avaliar")
    public ResponseEntity<DiariaResponseDTO> avaliar(
            @PathVariable Long id, 
            @RequestParam int nota) {
        DiariaResponseDTO avaliada = service.avaliarDiaria(id, nota);
        return ResponseEntity.ok(avaliada);
    }
}