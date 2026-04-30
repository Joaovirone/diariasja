package com.diariasja.aws.dto;

import java.time.LocalDate;

import com.diariasja.aws.entity.enums.TipoUsuario;

// Não devolvemos a senha!
public record UsuarioResponseDTO(
    Long id,
    String nome,
    String email,
    LocalDate dataNascimento,
    TipoUsuario tipo,
    boolean ativo
) {}