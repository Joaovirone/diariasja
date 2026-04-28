package com.diariasja.aws.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_categoria_servico")
@Data
public class CategoriaServico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String nome;

    private String descricao;
}
