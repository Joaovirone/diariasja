package com.diariasja.aws.entity;

import java.time.LocalDate;

import com.diariasja.aws.entity.enums.StatusDiaria;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_diarias")
@Data
public class Diaria extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contratante_id", nullable = false)
    private Usuario contratante;

    @ManyToOne
    @JoinColumn(name = "contratado_id", nullable = false)
    private Usuario contratado;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaServico categoria;

    @Column(nullable = false)
    private LocalDate dataServico;

    @Column(nullable= true)
    private Integer nota;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDiaria status;
}