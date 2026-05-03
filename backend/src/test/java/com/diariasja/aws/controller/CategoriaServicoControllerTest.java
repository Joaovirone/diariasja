package com.diariasja.aws.controller;

import java.time.LocalDate;

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

import com.diariasja.aws.dto.CategoriaServicoRequestDTO;
import com.diariasja.aws.entity.CategoriaServico;
import com.diariasja.aws.entity.Usuario;
import com.diariasja.aws.entity.enums.TipoUsuario;
import com.diariasja.aws.repository.CategoriaServicoRepository;
import com.diariasja.aws.repository.UsuarioRepository;
import com.diariasja.aws.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class CategoriaServicoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private CategoriaServicoRepository categoriaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private TokenService tokenService;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;

    private String tokenValido;
    private CategoriaServico categoriaExistente;

    @BeforeEach
    void setUp() {
        categoriaRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Criar usuário admin para token
        Usuario admin = new Usuario();
        admin.setNome("Admin");
        admin.setEmail("admin@test.com");
        admin.setSenha(passwordEncoder.encode("senha123"));
        admin.setDataNascimento(LocalDate.of(1990, 1, 1));
        admin.setTipo(TipoUsuario.CONTRATANTE);
        admin.setAtivo(true);
        usuarioRepository.save(admin);
        tokenValido = tokenService.gerarToken(admin);

        // Criar categoria existente
        categoriaExistente = new CategoriaServico();
        categoriaExistente.setNome("Limpeza");
        categoriaExistente.setDescricao("Serviços de limpeza residencial");
        categoriaRepository.save(categoriaExistente);
    }

    @Test
    void testCriarCategoriaComJWT() throws Exception {
        CategoriaServicoRequestDTO dto = new CategoriaServicoRequestDTO("Encanamento", "Serviços de encanamento");

        mockMvc.perform(post("/api/categorias")
                .header("Authorization", "Bearer " + tokenValido)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", equalTo("Encanamento")))
                .andExpect(jsonPath("$.descricao", equalTo("Serviços de encanamento")));
    }

    @Test
    void testCriarCategoriaSemJWT() throws Exception {
        CategoriaServicoRequestDTO dto = new CategoriaServicoRequestDTO("Encanamento", "Serviços de encanamento");

        mockMvc.perform(post("/api/categorias")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testBuscarCategoriaPorId() throws Exception {
        mockMvc.perform(get("/api/categorias/" + categoriaExistente.getId())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", equalTo("Limpeza")))
                .andExpect(jsonPath("$.descricao", equalTo("Serviços de limpeza residencial")));
    }

    @Test
    void testBuscarCategoriaInexistente() throws Exception {
        mockMvc.perform(get("/api/categorias/999")
                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testListarCategorias() throws Exception {
        mockMvc.perform(get("/api/categorias")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome", equalTo("Limpeza")));
    }

    @Test
    void testListarCategoriasComFiltroNome() throws Exception {
        mockMvc.perform(get("/api/categorias?nome=Limpeza")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nome", equalTo("Limpeza")));
    }

    @Test
    void testListarCategoriasComFiltrNomeNaoEncontrado() throws Exception {
        mockMvc.perform(get("/api/categorias?nome=Inexistente")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }
}
