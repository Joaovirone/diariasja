package com.diariasja.aws.config;

import com.diariasja.aws.entity.Usuario;
import com.diariasja.aws.entity.enums.TipoUsuario;
import com.diariasja.aws.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DadosIniciaisConfig {

    private static final String SENHA_PADRAO = "123456";

    @Bean
    CommandLineRunner criarUsuariosFixos(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            criarOuAtualizarUsuario(
                    usuarioRepository,
                    passwordEncoder,
                    "Contratante",
                    "joao@gmail.com",
                    TipoUsuario.CONTRATANTE,
                    LocalDate.of(1995, 1, 1)
            );

            criarOuAtualizarUsuario(
                    usuarioRepository,
                    passwordEncoder,
                    "Prestador de Serviço",
                    "pedro@gmail.com",
                    TipoUsuario.CONTRATADO,
                    LocalDate.of(1990, 1, 1)
            );
        };
    }

    private void criarOuAtualizarUsuario(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            String nome,
            String email,
            TipoUsuario tipo,
            LocalDate dataNascimento
    ) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElseGet(Usuario::new);

        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(SENHA_PADRAO));
        usuario.setDataNascimento(dataNascimento);
        usuario.setTipo(tipo);
        usuario.setAtivo(true);

        usuarioRepository.save(usuario);
    }
}
