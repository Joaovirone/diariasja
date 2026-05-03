package com.diariasja.aws.controller;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.diariasja.aws.dto.UsuarioRequestDTO;
import com.diariasja.aws.entity.Usuario;
import com.diariasja.aws.entity.enums.TipoUsuario;
import com.diariasja.aws.repository.UsuarioRepository;
import com.diariasja.aws.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class UsuarioControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private TokenService tokenService;
    @Autowired private ObjectMapper objectMapper;

    private String tokenValido;
    private Usuario usuarioPrestador;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        
        Usuario usuarioContratante = new Usuario();
        usuarioContratante.setNome("João Contratante");
        usuarioContratante.setEmail("contratante@test.com");
        usuarioContratante.setSenha(passwordEncoder.encode("senha123"));
        usuarioContratante.setDataNascimento(LocalDate.of(1990, 1, 1));
        usuarioContratante.setTipo(TipoUsuario.CONTRATANTE);
        usuarioContratante.setAtivo(true);
        usuarioRepository.save(usuarioContratante);
        tokenValido = tokenService.gerarToken(usuarioContratante);

        usuarioPrestador = new Usuario();
        usuarioPrestador.setNome("Maria Prestadora");
        usuarioPrestador.setEmail("prestadora@test.com");
        usuarioPrestador.setSenha(passwordEncoder.encode("senha123"));
        usuarioPrestador.setDataNascimento(LocalDate.of(1995, 5, 15));
        usuarioPrestador.setTipo(TipoUsuario.CONTRATADO);
        usuarioPrestador.setAtivo(true);
        usuarioRepository.save(usuarioPrestador);
    }

    @Test
    void testCadastrarUsuarioContratante() throws Exception {
        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Novo Contratante",
                "novo@test.com",
                "senha123456",
                LocalDate.of(1992, 3, 10),
                TipoUsuario.CONTRATANTE
        );

        mockMvc.perform(post("/api/usuarios/cadastrar")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", equalTo("Novo Contratante")))
                .andExpect(jsonPath("$.email", equalTo("novo@test.com")))
                .andExpect(jsonPath("$.tipo", equalTo("CONTRATANTE")));
    }

    @Test
    void testCadastrarUsuarioPrestador() throws Exception {
        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Novo Prestador",
                "prestador@test.com",
                "senha123456",
                LocalDate.of(1995, 6, 20),
                TipoUsuario.CONTRATADO
        );

        mockMvc.perform(post("/api/usuarios/cadastrar")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo", equalTo("CONTRATADO")));
    }

    @Test
    void testCadastrarComEmailDuplicado() throws Exception {
        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Outro Usuario",
                "prestadora@test.com", // Email já existe
                "senha123456",
                LocalDate.of(1995, 6, 20),
                TipoUsuario.CONTRATADO
        );

        mockMvc.perform(post("/api/usuarios/cadastrar")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem", containsString("Email já cadastrado")));
    }

    @Test
    void testCadastrarComEmailInvalido() throws Exception {
        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Usuario Novo",
                "email-invalido",
                "senha123456",
                LocalDate.of(1995, 6, 20),
                TipoUsuario.CONTRATADO
        );

        mockMvc.perform(post("/api/usuarios/cadastrar")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCadastrarComSenhaFreca() throws Exception {
        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Usuario Novo",
                "novo@test.com",
                "123", // Menos de 8 caracteres
                LocalDate.of(1995, 6, 20),
                TipoUsuario.CONTRATADO
        );

        mockMvc.perform(post("/api/usuarios/cadastrar")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testListarProfissionaisAtivos() throws Exception {
        mockMvc.perform(get("/api/usuarios/profissionais")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].tipo", equalTo("CONTRATADO")));
    }

    @Test
    void testBuscarMeuPerfil() throws Exception {
        mockMvc.perform(get("/api/usuarios/me")
                .header("Authorization", "Bearer " + tokenValido)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", equalTo("contratante@test.com")))
                .andExpect(jsonPath("$.tipo", equalTo("CONTRATANTE")));
    }

    @Test
    void testBuscarMeuPerfilSemJWT() throws Exception {
        mockMvc.perform(get("/api/usuarios/me")
                .contentType("application/json"))
                .andExpect(status().isUnauthorized());
    }
}
