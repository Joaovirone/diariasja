package com.diariasja.aws.repository;

import com.diariasja.aws.entity.CategoriaServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// JpaRepository exige dois parâmetros: A Entidade e o tipo do ID dela (Long)
public interface CategoriaServicoRepository extends JpaRepository<CategoriaServico, Long> {
    
    // O Spring gera o SQL automaticamente só pelo nome do método!
    Optional<CategoriaServico> findByNome(String nome);
}