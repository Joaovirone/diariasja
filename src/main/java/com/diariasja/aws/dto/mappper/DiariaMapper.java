package com.diariasja.aws.dto.mappper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.diariasja.aws.dto.DiariaResponseDTO;
import com.diariasja.aws.entity.Diaria;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DiariaMapper {

    // O MapStruct é inteligente, mas para objetos aninhados (Entidade dentro de Entidade),
    // precisamos dizer de onde ele vai puxar a String do nome.
    @Mapping(source = "contratante.nome", target = "nomeContratante")
    @Mapping(source = "contratado.nome", target = "nomeContratado")
    @Mapping(source = "categoria.nome", target = "nomeCategoria")
    DiariaResponseDTO toResponseDTO(Diaria entity);
}