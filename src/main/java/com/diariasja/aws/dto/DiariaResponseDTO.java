package com.diariasja.aws.dto;

import java.time.LocalDate;

import com.diariasja.aws.entity.enums.StatusDiaria;

// Uma resposta limpa com o resumo do que o front-end precisa
public record DiariaResponseDTO(
    Long id,
    String nomeContratante,
    String nomeContratado,
    String nomeCategoria,
    LocalDate dataServico,
    StatusDiaria status
) {}