package test.java.com.diariasja.aws.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.diariasja.aws.dto.DiariaRequestDTO;
import com.diariasja.aws.entity.CategoriaServico;
import com.diariasja.aws.entity.Diaria;
import com.diariasja.aws.entity.Usuario;
import com.diariasja.aws.entity.enums.StatusDiaria;
import com.diariasja.aws.entity.enums.TipoUsuario;
import com.diariasja.aws.repository.CategoriaServicoRepository;
import com.diariasja.aws.repository.DiariaRepository;
import com.diariasja.aws.repository.UsuarioRepository;
import com.diariasja.aws.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class DiariaControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private DiariaRepository diariaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CategoriaServicoRepository categoriaRepository;
    @Autowired private TokenService tokenService;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;

    private Usuario contratante;
    private Usuario prestador;
    private Usuario outroUsuario;
    private CategoriaServico categoria;
    private String tokenContratante;
    private String tokenPrestador;
    private Diaria diariaExistente;

    @BeforeEach
    void setUp() {
        diariaRepository.deleteAll();
        usuarioRepository.deleteAll();
        categoriaRepository.deleteAll();

        // Criar contratante
        contratante = new Usuario();
        contratante.setNome("João Contratante");
        contratante.setEmail("contratante@test.com");
        contratante.setSenha(passwordEncoder.encode("senha123"));
        contratante.setDataNascimento(LocalDate.of(1990, 1, 1));
        contratante.setTipo(TipoUsuario.CONTRATANTE);
        contratante.setAtivo(true);
        usuarioRepository.save(contratante);
        tokenContratante = tokenService.gerarToken(contratante);

        // Criar prestador
        prestador = new Usuario();
        prestador.setNome("Maria Prestadora");
        prestador.setEmail("prestadora@test.com");
        prestador.setSenha(passwordEncoder.encode("senha123"));
        prestador.setDataNascimento(LocalDate.of(1995, 5, 15));
        prestador.setTipo(TipoUsuario.CONTRATADO);
        prestador.setAtivo(true);
        usuarioRepository.save(prestador);
        tokenPrestador = tokenService.gerarToken(prestador);

        // Criar outro usuário para testes de autorização
        outroUsuario = new Usuario();
        outroUsuario.setNome("Outro Prestador");
        outroUsuario.setEmail("outro@test.com");
        outroUsuario.setSenha(passwordEncoder.encode("senha123"));
        outroUsuario.setDataNascimento(LocalDate.of(1993, 7, 20));
        outroUsuario.setTipo(TipoUsuario.CONTRATADO);
        outroUsuario.setAtivo(true);
        usuarioRepository.save(outroUsuario);

        // Criar categoria
        categoria = new CategoriaServico();
        categoria.setNome("Limpeza");
        categoria.setDescricao("Serviços de limpeza");
        categoriaRepository.save(categoria);

        // Criar diária existente (PENDENTE)
        diariaExistente = new Diaria();
        diariaExistente.setContratante(contratante);
        diariaExistente.setContratado(prestador);
        diariaExistente.setCategoria(categoria);
        diariaExistente.setDataServico(LocalDate.now().plusDays(5));
        diariaExistente.setStatus(StatusDiaria.PENDENTE);
        diariaRepository.save(diariaExistente);
    }

    @Test
    void testSolicitarDiariaComSucesso() throws Exception {
        DiariaRequestDTO dto = new DiariaRequestDTO(
                contratante.getId(),
                prestador.getId(),
                categoria.getId(),
                LocalDate.now().plusDays(3)
        );

        mockMvc.perform(post("/api/diarias/solicitar")
                .header("Authorization", "Bearer " + tokenContratante)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", equalTo("PENDENTE")));
    }

    @Test
    void testSolicitarDiariaComDataNoPassado() throws Exception {
        DiariaRequestDTO dto = new DiariaRequestDTO(
                contratante.getId(),
                prestador.getId(),
                categoria.getId(),
                LocalDate.now().minusDays(1) // Data no passado
        );

        mockMvc.perform(post("/api/diarias/solicitar")
                .header("Authorization", "Bearer " + tokenContratante)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSolicitarDiariaAutoContratacao() throws Exception {
        DiariaRequestDTO dto = new DiariaRequestDTO(
                contratante.getId(),
                contratante.getId(), // Mesmo usuário
                categoria.getId(),
                LocalDate.now().plusDays(3)
        );

        mockMvc.perform(post("/api/diarias/solicitar")
                .header("Authorization", "Bearer " + tokenContratante)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", containsString("Não pode contratar a si mesmo")));
    }

    @Test
    void testSolicitarDiariaComTipoErrado() throws Exception {
        // Tentar criar diária com contratante sendo PRESTADOR
        DiariaRequestDTO dto = new DiariaRequestDTO(
                prestador.getId(), // PRESTADOR, deveria ser CONTRATANTE
                contratante.getId(),
                categoria.getId(),
                LocalDate.now().plusDays(3)
        );

        mockMvc.perform(post("/api/diarias/solicitar")
                .header("Authorization", "Bearer " + tokenContratante)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", containsString("CONTRATANTE")));
    }

    @Test
    void testAceitarDiariaComSucesso() throws Exception {
        mockMvc.perform(patch("/api/diarias/" + diariaExistente.getId() + "/aceitar?idProfissional=" + prestador.getId())
                .header("Authorization", "Bearer " + tokenPrestador)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo("CONFIRMADA")));
    }

    @Test
    void testAceitarDiariaComIdProfissionalErrado() throws Exception {
        // Prestador tenta aceitar com ID de outro
        mockMvc.perform(patch("/api/diarias/" + diariaExistente.getId() + "/aceitar?idProfissional=" + outroUsuario.getId())
                .header("Authorization", "Bearer " + tokenPrestador)
                .contentType("application/json"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAceitarDiariaSemJWT() throws Exception {
        mockMvc.perform(patch("/api/diarias/" + diariaExistente.getId() + "/aceitar?idProfissional=" + prestador.getId())
                .contentType("application/json"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAvaliarDiariaComNotaValida() throws Exception {
        // Primeiro completar a diária
        diariaExistente.setStatus(StatusDiaria.CONCLUIDA);
        diariaRepository.save(diariaExistente);

        mockMvc.perform(patch("/api/diarias/" + diariaExistente.getId() + "/avaliar?nota=5")
                .header("Authorization", "Bearer " + tokenContratante)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nota", equalTo(5)));
    }

    @Test
    void testAvaliarDiariaComNotaInvalida() throws Exception {
        diariaExistente.setStatus(StatusDiaria.CONCLUIDA);
        diariaRepository.save(diariaExistente);

        mockMvc.perform(patch("/api/diarias/" + diariaExistente.getId() + "/avaliar?nota=10")
                .header("Authorization", "Bearer " + tokenContratante)
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", containsString("1 e 5")));
    }

    @Test
    void testCancelarDiariaComoContratante() throws Exception {
        mockMvc.perform(patch("/api/diarias/" + diariaExistente.getId() + "/cancelar?idUsuario=" + contratante.getId())
                .header("Authorization", "Bearer " + tokenContratante)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo("CANCELADA")));
    }

    @Test
    void testCancelarDiariaComIdUsuarioErrado() throws Exception {
        // Contratante tenta cancelar como outro
        mockMvc.perform(patch("/api/diarias/" + diariaExistente.getId() + "/cancelar?idUsuario=" + outroUsuario.getId())
                .header("Authorization", "Bearer " + tokenContratante)
                .contentType("application/json"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testListarDiariasDoContratante() throws Exception {
        mockMvc.perform(get("/api/diarias/contratante/" + contratante.getId())
                .header("Authorization", "Bearer " + tokenContratante)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].status", equalTo("PENDENTE")));
    }

    @Test
    void testListarDiariasPendentesDoPrestador() throws Exception {
        mockMvc.perform(get("/api/diarias/profissional/" + prestador.getId() + "/pendentes")
                .header("Authorization", "Bearer " + tokenPrestador)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }
}
