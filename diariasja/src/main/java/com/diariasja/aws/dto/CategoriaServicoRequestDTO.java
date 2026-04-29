package com.diariasja.aws.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaServicoRequestDTO(
    
    @NotBlank(message = "O nome da categoria é obrigatório")
    String nome,
    
    String descricao
) {}