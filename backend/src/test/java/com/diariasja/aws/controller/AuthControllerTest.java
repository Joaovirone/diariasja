package com.diariasja.aws.controller;

import java.time.LocalDate;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.diariasja.aws.dto.LoginRequestDTO;
import com.diariasja.aws.entity.Usuario;
import com.diariasja.aws.entity.enums.TipoUsuario;
import com.diariasja.aws.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.awspring.cloud.sqs.operations.SqsTemplate;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private SqsTemplate sqsTemplate;

    private Usuario usuarioValido;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        
        usuarioValido = new Usuario();
        usuarioValido.setNome("João Silva");
        usuarioValido.setEmail("joao@test.com");
        usuarioValido.setSenha(passwordEncoder.encode("senha123"));
        usuarioValido.setDataNascimento(LocalDate.of(1990, 1, 1));
        usuarioValido.setTipo(TipoUsuario.CONTRATANTE);
        usuarioValido.setAtivo(true);
        usuarioRepository.save(usuarioValido);
    }

    @Test
    void testLoginComCredenciaisValidas() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO("joao@test.com", "senha123");

        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(notNullValue()));
    }

    @Test
    void testLoginComEmailInexistente() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO("inexistente@test.com", "senha123");

        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem", equalTo("Credenciais inválidas")));
    }

    @Test
    void testLoginComSenhaIncorreta() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO("joao@test.com", "senhaErrada");

        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(equalTo("Credenciais inválidas")));
    }

    @Test
    void testLoginComEmailVazio() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO("", "senha123");

        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginComSenhaVazia() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO("joao@test.com", "");

        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
