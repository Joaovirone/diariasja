package com.diariasja.aws.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass // Diz ao Hibernate: "Não cries uma tabela para isto, apenas empresta estes campos às classes filhas"
@EntityListeners(AuditingEntityListener.class) // Avisa o Spring para preencher as datas automaticamente
public abstract class EntidadeAuditavel {

    @CreatedDate
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao; // Preenchido apenas no INSERT

    @LastModifiedDate
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao; // Atualizado em cada UPDATE
}