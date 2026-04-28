package com.diariasja.aws.service;

import com.diariasja.aws.entity.Usuario;
import com.diariasja.aws.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Transactional // Se der erro no meio do processo, ele desfaz a operação no banco (Rollback)
    public Usuario cadastrar(Usuario usuario) {
        // 1. Regra de Negócio: Validar idade (RN01)
        // int idade = Period.between(usuario.getDataNascimento(), LocalDate.now()).getYears();
        // se idade < 18, lançar exceção.

        // 2. Aqui, futuramente, faremos o hash da senha (Bcrypt) antes de salvar
        
        return repository.save(usuario);
    }
}