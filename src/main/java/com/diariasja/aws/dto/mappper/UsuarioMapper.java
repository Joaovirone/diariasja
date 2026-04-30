package com.diariasja.aws.dto.mappper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.diariasja.aws.dto.UsuarioRequestDTO;
import com.diariasja.aws.dto.UsuarioResponseDTO;
import com.diariasja.aws.entity.Usuario;

// componentModel = "spring" avisa o Spring para transformar esse mapper em um @Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UsuarioMapper {

    Usuario toEntity(UsuarioRequestDTO dto);
    
    UsuarioResponseDTO toResponseDTO(Usuario entity);
}