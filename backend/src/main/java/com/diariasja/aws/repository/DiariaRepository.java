package com.diariasja.aws.repository;

import com.diariasja.aws.entity.Diaria;
import com.diariasja.aws.entity.enums.StatusDiaria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiariaRepository extends JpaRepository<Diaria, Long> {

    // Retorna o histórico de diárias de um cliente paginado (ex: 10 por página)
    Page<Diaria> findByContratanteId(Long contratanteId, Pageable pageable);

    // Retorna a agenda de um prestador de serviço
    Page<Diaria> findByContratadoId(Long contratadoId, Pageable pageable);

    // Retorna a agenda de um prestador de serviço filtrando por Status
    Page<Diaria> findByContratadoIdAndStatus(Long contratadoId, StatusDiaria status, Pageable pageable);
}
