package com.diariasja.aws.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diariasja.aws.dto.DiariaRequestDTO;
import com.diariasja.aws.dto.DiariaResponseDTO;
import com.diariasja.aws.dto.mappper.DiariaMapper; // Mantive com 3 'p's conforme o seu código
import com.diariasja.aws.entity.CategoriaServico;
import com.diariasja.aws.entity.Diaria;
import com.diariasja.aws.entity.Usuario;
import com.diariasja.aws.entity.enums.StatusDiaria;
import com.diariasja.aws.exception.ResourceNotFoundException;
import com.diariasja.aws.repository.CategoriaServicoRepository;
import com.diariasja.aws.repository.DiariaRepository;
import com.diariasja.aws.repository.UsuarioRepository;

@Service
public class DiariaService {

    @Autowired private DiariaRepository diariaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CategoriaServicoRepository categoriaRepository;
    @Autowired private DiariaMapper mapper;
    
    // Injetando o serviço de Fila SQS (AWS) para o momento do aceite
    @Autowired private NotificacaoService notificacaoService;

    // --- 1. REGRA: SOLICITAR SERVIÇO ---
    @Transactional
    public DiariaResponseDTO solicitarServico(DiariaRequestDTO dto) { 
        Usuario contratante = usuarioRepository.findById(dto.contratanteId())
            .orElseThrow(() -> new ResourceNotFoundException("Contratante não encontrado"));
            
        Usuario contratado = usuarioRepository.findById(dto.contratadoId())
            .orElseThrow(() -> new ResourceNotFoundException("Contratado não encontrado"));
            
        CategoriaServico categoria = categoriaRepository.findById(dto.categoriaId())
            .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        Diaria diaria = new Diaria();
        diaria.setContratante(contratante);
        diaria.setContratado(contratado);
        diaria.setCategoria(categoria);
        diaria.setDataServico(dto.dataServico());
        diaria.setStatus(StatusDiaria.PENDENTE); 

        Diaria salva = diariaRepository.save(diaria);
        return mapper.toResponseDTO(salva); 
    }

    // --- 2. REGRA: ACEITAR DIÁRIA (Com Fila AWS) ---
    @Transactional
    public DiariaResponseDTO aceitarDiaria(Long idDiaria, Long idProfissional) {
        Diaria diaria = diariaRepository.findById(idDiaria)
            .orElseThrow(() -> new ResourceNotFoundException("Diária não encontrada"));

        if (!diaria.getContratado().getId().equals(idProfissional)) {
            throw new IllegalArgumentException("Apenas o profissional escalado pode aceitar.");
        }
        if (diaria.getStatus() != StatusDiaria.PENDENTE) {
            throw new IllegalArgumentException("Apenas diárias pendentes podem ser aceitas.");
        }

        diaria.setStatus(StatusDiaria.CONFIRMADA);
        Diaria salva = diariaRepository.save(diaria);

        // Dispara mensagem assíncrona
        String msg = "O profissional " + diaria.getContratado().getNome() + " aceitou sua solicitação!";
        notificacaoService.notificarContratante(diaria.getContratante().getEmail(), msg);

        return mapper.toResponseDTO(salva);
    }

    // --- 3. REGRA: AVALIAR SERVIÇO ---
    @Transactional
    public DiariaResponseDTO avaliarDiaria(Long idDiaria, int nota) {
        Diaria diaria = diariaRepository.findById(idDiaria)
            .orElseThrow(() -> new ResourceNotFoundException("Diária não encontrada"));

        if (diaria.getStatus() != StatusDiaria.CONCLUIDA) {
            throw new IllegalArgumentException("Apenas serviços concluídos podem ser avaliados.");
        }
        if (nota < 1 || nota > 5) {
            throw new IllegalArgumentException("A nota deve ser entre 1 e 5 estrelas.");
        }

        // Se você já adicionou "private Integer nota;" na entidade Diaria, descomente a linha abaixo:
        // diaria.setNota(nota); 
        
        return mapper.toResponseDTO(diariaRepository.save(diaria));
    }

    // --- 4. CONSULTAS PAGINADAS ---
    @Transactional(readOnly = true)
    public Page<DiariaResponseDTO> listarMinhasDiariasComoContratante(Long contratanteId, Pageable pageable) {
        Page<Diaria> diarias = diariaRepository.findByContratanteId(contratanteId, pageable);
        return diarias.map(mapper::toResponseDTO); 
    }

    @Transactional(readOnly = true)
    public Page<DiariaResponseDTO> listarDiariasPendentesDoProfissional(Long contratadoId, Pageable pageable) {
        Page<Diaria> diarias = diariaRepository.findByContratadoIdAndStatus(contratadoId, StatusDiaria.PENDENTE, pageable);
        return diarias.map(mapper::toResponseDTO);
    }
}