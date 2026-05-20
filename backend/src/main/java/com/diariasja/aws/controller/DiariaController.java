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
import org.springframework.web.bind.annotation.PatchMapping; // Importante: trazer o DTO de resposta
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.diariasja.aws.dto.DiariaRequestDTO;
import com.diariasja.aws.dto.DiariaResponseDTO;
import com.diariasja.aws.entity.Usuario;
import com.diariasja.aws.exception.ResourceNotFoundException;
import com.diariasja.aws.repository.UsuarioRepository;
import com.diariasja.aws.service.DiariaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/diarias")
public class DiariaController {

    @Autowired private DiariaService service;
    @Autowired private UsuarioRepository usuarioRepository;

    @PostMapping("/solicitar")
    public ResponseEntity<DiariaResponseDTO> solicitar(@Valid @RequestBody DiariaRequestDTO dto) {
        DiariaResponseDTO novaDiaria = service.solicitarServico(dto);
        return new ResponseEntity<>(novaDiaria, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/aceitar")
    public ResponseEntity<DiariaResponseDTO> aceitar(@PathVariable Long id, @RequestParam(name = "idProfissional") Long idPrestador) {
        // Extrair email do JWT via SecurityContextHolder
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Buscar o usuário para obter o ID
        Usuario usuarioLogado = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário logado não encontrado"));
        
        // Validar se o usuário logado é o prestador de serviço que está tentando aceitar
        if (!usuarioLogado.getId().equals(idPrestador)) {
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }
        
        return ResponseEntity.ok(service.aceitarDiaria(id, idPrestador));
    }

    @PatchMapping("/{id}/avaliar")
    public ResponseEntity<DiariaResponseDTO> avaliar(@PathVariable Long id, @RequestParam int nota) {
        return ResponseEntity.ok(service.avaliarDiaria(id, nota));
    }

    // --- NOVOS ENDPOINTS ---

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<DiariaResponseDTO> cancelar(@PathVariable Long id, @RequestParam Long idUsuario) {
        // Extrair email do JWT via SecurityContextHolder
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Buscar o usuário para obter o ID
        Usuario usuarioLogado = usuarioRepository.findByEmail(emailLogado)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário logado não encontrado"));
        
        // Validar se o usuário logado é o contratante que está tentando cancelar
        if (!usuarioLogado.getId().equals(idUsuario)) {
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }
        
        return ResponseEntity.ok(service.cancelarDiaria(id, idUsuario));
    }

    @GetMapping("/contratante/{contratanteId}")
    public ResponseEntity<Page<DiariaResponseDTO>> listarDoContratante(
            @PathVariable Long contratanteId,
            @ParameterObject
            @PageableDefault(size = 10, sort = "dataServico") Pageable pageable) {
        return ResponseEntity.ok(service.listarMinhasDiariasComoContratante(contratanteId, pageable));
    }

    @GetMapping("/profissional/{contratadoId}/pendentes")
    public ResponseEntity<Page<DiariaResponseDTO>> listarPendentes(
            @PathVariable Long contratadoId,
            @ParameterObject
            @PageableDefault(size = 10, sort = "dataServico") Pageable pageable) {
        return ResponseEntity.ok(service.listarDiariasPendentesDoPrestador(contratadoId, pageable));
    }
}
