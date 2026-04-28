package com.diariasja.aws.repository;

import com.diariasja.aws.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Essencial para o login: buscar o usuário pelo e-mail
    Optional<Usuario> findByEmail(String email);
}