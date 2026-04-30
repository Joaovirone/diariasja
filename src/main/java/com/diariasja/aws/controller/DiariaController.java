package com.diariasja.aws.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // Importante: trazer o DTO de resposta
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.diariasja.aws.dto.DiariaRequestDTO;
import com.diariasja.aws.dto.DiariaResponseDTO;
import com.diariasja.aws.service.DiariaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/diarias")
public class DiariaController {

    @Autowired private DiariaService service;

    @PostMapping("/solicitar")
    public ResponseEntity<DiariaResponseDTO> solicitar(@Valid @RequestBody DiariaRequestDTO dto) {
        DiariaResponseDTO novaDiaria = service.solicitarServico(dto);
        return new ResponseEntity<>(novaDiaria, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/aceitar")
    public ResponseEntity<DiariaResponseDTO> aceitar(@PathVariable Long id, @RequestParam Long idProfissional) {
        return ResponseEntity.ok(service.aceitarDiaria(id, idProfissional));
    }

    @PatchMapping("/{id}/avaliar")
    public ResponseEntity<DiariaResponseDTO> avaliar(@PathVariable Long id, @RequestParam int nota) {
        return ResponseEntity.ok(service.avaliarDiaria(id, nota));
    }

    // --- NOVOS ENDPOINTS ---

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<DiariaResponseDTO> cancelar(@PathVariable Long id, @RequestParam Long idUsuario) {
        return ResponseEntity.ok(service.cancelarDiaria(id, idUsuario));
    }

    @GetMapping("/contratante/{contratanteId}")
    public ResponseEntity<Page<DiariaResponseDTO>> listarDoContratante(
            @PathVariable Long contratanteId,
            @PageableDefault(size = 10, sort = "dataServico") Pageable pageable) {
        return ResponseEntity.ok(service.listarMinhasDiariasComoContratante(contratanteId, pageable));
    }

    @GetMapping("/profissional/{contratadoId}/pendentes")
    public ResponseEntity<Page<DiariaResponseDTO>> listarPendentes(
            @PathVariable Long contratadoId,
            @PageableDefault(size = 10, sort = "dataServico") Pageable pageable) {
        return ResponseEntity.ok(service.listarDiariasPendentesDoProfissional(contratadoId, pageable));
    }
}