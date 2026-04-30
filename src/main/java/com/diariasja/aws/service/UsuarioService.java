package com.diariasja.aws.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diariasja.aws.dto.UsuarioRequestDTO;
import com.diariasja.aws.dto.UsuarioResponseDTO;
import com.diariasja.aws.dto.mappper.UsuarioMapper;
import com.diariasja.aws.entity.Usuario;
import com.diariasja.aws.entity.enums.TipoUsuario;
import com.diariasja.aws.exception.ResourceNotFoundException;
import com.diariasja.aws.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioMapper mapper;

    @Autowired 
    private PasswordEncoder passwordEncoder; 

     @Transactional
        public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {
            Usuario usuario = mapper.toEntity(dto);
            
            // CRIPTOGRAFIA NA PRÁTICA: Substitui a senha limpa pelo Hash do BCrypt
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
            
            Usuario usuarioSalvo = repository.save(usuario);
            return mapper.toResponseDTO(usuarioSalvo);
        }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarProfissionaisAtivos(Pageable pageable) {
        // Busca apenas usuários do tipo CONTRATADO e que estão ativos
        Page<Usuario> profissionais = repository.findByTipoAndAtivoTrue(TipoUsuario.CONTRATADO, pageable);
        return profissionais.map(mapper::toResponseDTO);
    }

    public UsuarioResponseDTO buscarPorEmail(String email) {
        Usuario usuario = repository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return mapper.toResponseDTO(usuario);
    }

}