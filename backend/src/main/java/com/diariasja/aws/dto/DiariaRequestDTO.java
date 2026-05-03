package com.diariasja.aws.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public record DiariaRequestDTO (

    @NotNull 
    Long contratanteId,
    
    @NotNull 
    Long contratadoId,
    
    @NotNull 
    Long categoriaId,
    
    @NotNull
    @FutureOrPresent
    LocalDate dataServico

) {
    
}
