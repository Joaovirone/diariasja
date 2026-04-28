package com.diariasja.aws;

import com.diariasja.aws.entity.CategoriaServico;
import com.diariasja.aws.exception.ResourceNotFoundException;
import com.diariasja.aws.repository.CategoriaServicoRepository;
import com.diariasja.aws.service.CategoriaServicoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) // Habilita o Mockito neste teste
public class CategoriaServicoServiceTest {

    // @InjectMocks cria uma instância real do Service, 
    // mas injeta os "dublês" (Mocks) dentro dele no lugar das dependências reais.
    @InjectMocks
    private CategoriaServicoService service;

    // @Mock cria o "dublê" do banco de dados. Ele não conecta no MySQL ou AWS RDS.
    @Mock
    private CategoriaServicoRepository repository;

    private CategoriaServico categoriaPadrao;

    @BeforeEach // Roda antes de cada @Test para preparar os dados
    void setUp() {
        categoriaPadrao = new CategoriaServico();
        categoriaPadrao.setId(1L);
        categoriaPadrao.setNome("Faxina");
        categoriaPadrao.setDescricao("Limpeza geral");
    }

    @Test
    @DisplayName("Deve retornar uma categoria quando o ID existir no banco")
    void buscarPorId_ComSucesso() {
        // 1. ARRANGE (Preparação): Ensinamos o dublê (Mock) o que ele deve fazer.
        // "Quando o service chamar repository.findById(1L), devolva a categoriaPadrao"
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(categoriaPadrao));

        // 2. ACT (Ação): Executamos o método real da nossa regra de negócio
        CategoriaServico resultado = service.buscarPorId(1L);

        // 3. ASSERT (Verificação): Confirmamos se a lógica funcionou como esperado
        assertNotNull(resultado); // Garante que não veio nulo
        assertEquals("Faxina", resultado.getNome()); // Garante que o nome veio correto
        assertEquals(1L, resultado.getId());
        
        // Verifica se o método do banco foi chamado exatamente 1 vez
        Mockito.verify(repository, Mockito.times(1)).findById(1L); 
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID não existir")
    void buscarPorId_IdNaoExiste_LancaExcecao() {
        // 1. ARRANGE: "Quando pedirem o ID 99, simule que o banco não achou nada (Optional.empty)"
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        // 2 & 3. ACT & ASSERT juntos: Verificamos se ao chamar com ID 99, a nossa exceção customizada é lançada
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            service.buscarPorId(99L);
        });

        // Garantimos que a mensagem de erro formatada para o usuário está correta
        assertEquals("Categoria não encontrada com ID: 99", exception.getMessage());
    }
}