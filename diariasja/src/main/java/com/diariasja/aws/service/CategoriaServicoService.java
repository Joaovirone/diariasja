package com.diariasja.aws.service;

import com.diariasja.aws.dto.CategoriaServicoRequestDTO;
import com.diariasja.aws.dto.CategoriaServicoResponseDTO;
import com.diariasja.aws.entity.CategoriaServico;
import com.diariasja.aws.exception.ResourceNotFoundException;
import com.diariasja.aws.dto.mappper.*;
import com.diariasja.aws.repository.CategoriaServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaServicoService {

    @Autowired
    private CategoriaServicoRepository repository;

    @Autowired
    private CategoriaServicoMapper mapper;

    @Transactional
    public CategoriaServicoResponseDTO criar(CategoriaServicoRequestDTO dto) {
        // Converte o JSON de entrada para a Entidade
        CategoriaServico categoria = mapper.toEntity(dto);
        
        // Salva no banco de dados
        CategoriaServico categoriaSalva = repository.save(categoria);
        
        // Converte a Entidade salva para o JSON de saída
        return mapper.toResponseDTO(categoriaSalva);
    }

    public List<CategoriaServicoResponseDTO> listarTodas() {
        List<CategoriaServico> categorias = repository.findAll();
        
        // Converte a lista de entidades para lista de DTOs
        return categorias.stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public CategoriaServicoResponseDTO buscarPorId(Long id) {
        CategoriaServico categoria = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));
                
        return mapper.toResponseDTO(categoria);
    }
    
    @Transactional(readOnly = true)
    public Page<CategoriaServicoResponseDTO> listarComPaginacao(String nome, Pageable pageable) {
        Page<CategoriaServico> categorias;
        
        if (nome != null && !nome.isEmpty()) {
            categorias = repository.findByNomeContainingIgnoreCase(nome, pageable);
        } else {
            categorias = repository.findAll(pageable);
        }
        
        return categorias.map(mapper::toResponseDTO);
    }
}