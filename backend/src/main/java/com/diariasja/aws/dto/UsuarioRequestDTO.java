package com.diariasja.aws.dto;

import java.time.LocalDate;

import com.diariasja.aws.entity.enums.TipoUsuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(

    @NotBlank(message = "O nome é obrigatório")
     String nome,

     @Email(message= "Email inválido")
     @NotBlank
     String email,

     @NotBlank @Size( min= 8, message= "A senha deve conter no mínimo 8 caracteres")
     String senha,

     @NotNull(message = "Data de nascimento obrigatória")
     LocalDate dataNascimento,

     @NotNull(message= "Escolha um Tipo de Usuário: CONTRATANTE | PRESTADOR DE SERVIÇOS")
     TipoUsuario tipo
) {
    
}
