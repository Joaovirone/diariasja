package com.diariasja.aws.repository;

import com.diariasja.aws.entity.Usuario;
import com.diariasja.aws.entity.enums.TipoUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);

    // Lista prestadores de serviço disponíveis e com paginação
    Page<Usuario> findByTipoAndAtivoTrue(TipoUsuario tipo, Pageable pageable);
}
