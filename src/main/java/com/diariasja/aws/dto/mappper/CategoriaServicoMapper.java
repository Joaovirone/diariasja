package com.diariasja.aws.dto.mappper;

import com.diariasja.aws.dto.CategoriaServicoRequestDTO;
import com.diariasja.aws.dto.CategoriaServicoResponseDTO;
import com.diariasja.aws.entity.CategoriaServico;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoriaServicoMapper {

    CategoriaServico toEntity(CategoriaServicoRequestDTO dto);
    
    CategoriaServicoResponseDTO toResponseDTO(CategoriaServico entity);
}