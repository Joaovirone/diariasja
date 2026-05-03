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
import com.diariasja.aws.entity.enums.TipoUsuario;
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
        // Validar que não pode contratar a si mesmo
        if (dto.contratanteId().equals(dto.contratadoId())) {
            throw new IllegalArgumentException("Não pode contratar a si mesmo");
        }
        
        Usuario contratante = usuarioRepository.findById(dto.contratanteId())
            .orElseThrow(() -> new ResourceNotFoundException("Contratante não encontrado"));
            
        Usuario contratado = usuarioRepository.findById(dto.contratadoId())
            .orElseThrow(() -> new ResourceNotFoundException("Contratado não encontrado"));
            
        CategoriaServico categoria = categoriaRepository.findById(dto.categoriaId())
            .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        // Validar tipos de usuário
        if (!TipoUsuario.CONTRATANTE.equals(contratante.getTipo())) {
            throw new IllegalArgumentException("Usuário contratante deve ser do tipo CONTRATANTE");
        }
        
        if (!TipoUsuario.CONTRATADO.equals(contratado.getTipo())) {
            throw new IllegalArgumentException("Usuário contratado deve ser do tipo PRESTADOR");
        }

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

         diaria.setNota(nota); 
        
        return mapper.toResponseDTO(diariaRepository.save(diaria));
    }

    @Transactional
    public DiariaResponseDTO cancelarDiaria(Long idDiaria, Long idUsuarioLogado) {
        Diaria diaria = diariaRepository.findById(idDiaria)
            .orElseThrow(() -> new ResourceNotFoundException("Diária não encontrada"));

        // Regra: Só quem criou a diária (ou um admin) pode cancelar
        if (!diaria.getContratante().getId().equals(idUsuarioLogado)) {
            throw new IllegalArgumentException("Você não tem permissão para cancelar esta diária.");
        }
        
        // Regra: Não pode cancelar se já estiver concluída ou cancelada
        if (diaria.getStatus() == StatusDiaria.CONCLUIDA || diaria.getStatus() == StatusDiaria.CANCELADA) {
            throw new IllegalArgumentException("Esta diária não pode mais ser cancelada.");
        }

        diaria.setStatus(StatusDiaria.CANCELADA);
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