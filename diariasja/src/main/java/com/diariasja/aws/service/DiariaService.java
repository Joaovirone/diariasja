package com.diariasja.aws.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diariasja.aws.dto.DiariaRequestDTO;
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

    @Transactional
    public Diaria solicitarServico(DiariaRequestDTO dto) {
        
        // 1. Busca todos os envolvidos ou lança erro 404
        Usuario contratante = usuarioRepository.findById(dto.contratanteId())
            .orElseThrow(() -> new ResourceNotFoundException("Contratante não encontrado"));
            
        Usuario contratado = usuarioRepository.findById(dto.contratadoId())
            .orElseThrow(() -> new ResourceNotFoundException("Contratado não encontrado"));
            
        CategoriaServico categoria = categoriaRepository.findById(dto.categoriaId())
            .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        // 2. Monta a Entidade para salvar no banco
        Diaria diaria = new Diaria();
        diaria.setContratante(contratante);
        diaria.setContratado(contratado);
        diaria.setCategoria(categoria);
        diaria.setDataServico(dto.dataServico());
        diaria.setStatus(StatusDiaria.PENDENTE); // Regra: Todo serviço nasce pendente

        // 3. Salva no RDS
        return diariaRepository.save(diaria);
        
        // Futuramente de DevOps: Aqui acionaremos o Amazon SQS para notificar o contratado!
    }
}