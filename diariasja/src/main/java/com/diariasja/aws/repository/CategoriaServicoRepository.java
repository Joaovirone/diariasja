package com.diariasja.aws.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diariasja.aws.entity.CategoriaServico;

@Repository
// JpaRepository exige dois parâmetros: A Entidade e o tipo do ID dela (Long)
public interface CategoriaServicoRepository extends JpaRepository<CategoriaServico, Long> {
    
    // O Spring gera o SQL automaticamente só pelo nome do método!
    Page<CategoriaServico> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}