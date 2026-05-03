package com.diariasja.aws.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

import com.diariasja.aws.entity.enums.TipoUsuario;

@Entity
@Table(name = "tb_usuarios")
@Data
public class Usuario extends EntidadeAuditavel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha; 

    @Column(nullable = false)
    private LocalDate dataNascimento; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipo; 

    private boolean ativo = true; 

    
}