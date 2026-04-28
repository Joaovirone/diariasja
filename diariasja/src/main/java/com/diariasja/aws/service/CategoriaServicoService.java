package com.diariasja.aws.service;

import com.diariasja.aws.entity.CategoriaServico;
import com.diariasja.aws.exception.ResourceNotFoundException;
import com.diariasja.aws.repository.CategoriaServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Indica que esta classe contém a lógica de negócios
public class CategoriaServicoService {

    @Autowired // O Spring injeta o repositório aqui automaticamente
    private CategoriaServicoRepository repository;

    public CategoriaServico criar(CategoriaServico categoria) {
        // Futuramente: validar se já existe uma categoria com esse nome
        return repository.save(categoria);
    }

    public List<CategoriaServico> listarTodas() {
        return repository.findAll();
    }

    public CategoriaServico buscarPorId(Long id) {
        // Se não achar o ID no banco, lança nossa exceção customizada!
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));
    }
}