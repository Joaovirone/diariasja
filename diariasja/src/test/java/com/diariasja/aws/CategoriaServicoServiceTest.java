package com.diariasja.aws;

import com.diariasja.aws.dto.CategoriaServicoResponseDTO;
import com.diariasja.aws.dto.mappper.CategoriaServicoMapper;
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

@ExtendWith(MockitoExtension.class)
public class CategoriaServicoServiceTest {

    @InjectMocks
    private CategoriaServicoService service;

    @Mock
    private CategoriaServicoRepository repository;

    // Agora precisamos "mockar" o Mapper, pois o Service o utiliza
    @Mock
    private CategoriaServicoMapper mapper;

    private CategoriaServico categoriaPadrao;
    private CategoriaServicoResponseDTO responsePadrao;

    @BeforeEach
    void setUp() {
        // Entidade que o banco devolveria
        categoriaPadrao = new CategoriaServico();
        categoriaPadrao.setId(1L);
        categoriaPadrao.setNome("Faxina");
        categoriaPadrao.setDescricao("Limpeza geral");

        // DTO que o Mapper deve gerar
        responsePadrao = new CategoriaServicoResponseDTO(1L, "Faxina", "Limpeza geral");
    }

    @Test
    @DisplayName("Deve retornar um DTO de categoria quando o ID existir no banco")
    void buscarPorId_ComSucesso() {
        // 1. ARRANGE
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(categoriaPadrao));
        // Ensinamos o Mockito a converter a entidade no DTO
        Mockito.when(mapper.toResponseDTO(categoriaPadrao)).thenReturn(responsePadrao);

        // 2. ACT - Agora recebe o ResponseDTO!
        CategoriaServicoResponseDTO resultado = service.buscarPorId(1L);

        // 3. ASSERT
        assertNotNull(resultado);
        // Como o DTO é um 'record', acessamos as propriedades como métodos: .nome() em vez de .getNome()
        assertEquals("Faxina", resultado.nome()); 
        assertEquals(1L, resultado.id());
        
        Mockito.verify(repository, Mockito.times(1)).findById(1L); 
        Mockito.verify(mapper, Mockito.times(1)).toResponseDTO(categoriaPadrao);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID não existir")
    void buscarPorId_IdNaoExiste_LancaExcecao() {
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            service.buscarPorId(99L);
        });

        assertEquals("Categoria não encontrada com ID: 99", exception.getMessage());
    }
}