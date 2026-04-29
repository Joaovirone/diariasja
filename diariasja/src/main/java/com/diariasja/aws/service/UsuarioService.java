package com.diariasja.aws.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diariasja.aws.dto.UsuarioRequestDTO;
import com.diariasja.aws.dto.UsuarioResponseDTO;
import com.diariasja.aws.dto.mappper.UsuarioMapper;
import com.diariasja.aws.entity.Usuario;
import com.diariasja.aws.entity.enums.TipoUsuario;
import com.diariasja.aws.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioMapper mapper;

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {
        // 1. Converte DTO para Entidade numa linha só
        Usuario usuario = mapper.toEntity(dto);
        
        // Futuro: Criptografar a senha aqui
        
        // 2. Salva no banco
        Usuario usuarioSalvo = repository.save(usuario);
        
        // 3. Devolve um ResponseDTO seguro e sem a senha
        return mapper.toResponseDTO(usuarioSalvo);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarProfissionaisAtivos(Pageable pageable) {
        // Busca apenas usuários do tipo CONTRATADO e que estão ativos
        Page<Usuario> profissionais = repository.findByTipoAndAtivoTrue(TipoUsuario.CONTRATADO, pageable);
        return profissionais.map(mapper::toResponseDTO);
    }
}