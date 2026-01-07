package br.com.fsj.salesplatform.controller;

import br.com.fsj.salesplatform.dto.ProductResponse;
import br.com.fsj.salesplatform.exception.GlobalExceptionHandler;
import br.com.fsj.salesplatform.exception.ResourceNotFoundException;
import br.com.fsj.salesplatform.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para ProductController.
 */
@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("ProductController - Testes de Integração")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService service;

    private ProductResponse productResponse1;
    private ProductResponse productResponse2;

    @BeforeEach
    void setUp() {
        productResponse1 = new ProductResponse(
                "1",
                "Dipirona Sódica 500mg",
                "Medley",
                "Analgésicos",
                "Antitérmicos",
                "Genérico",
                new BigDecimal("8.50"),
                new BigDecimal("5.20"),
                45,
                Map.of("Loja 2", 30, "Loja 3", 25),
                true,
                LocalDate.of(2025, 12, 31),
                "https://dummyimage.com/300x300/4A90E2/ffffff&text=Dipirona+500mg",
                "Dipirona Sódica 500mg - PBM",
                "Desconto de 40% com PBM",
                false,
                "Receita Branca Simples",
                false,
                "https://example.com/bulas/dipirona.pdf",
                null,
                LocalDate.of(2025, 12, 20),
                "À vista, 2x, 3x sem juros",
                false,
                false
        );

        productResponse2 = new ProductResponse(
                "2",
                "Paracetamol 750mg",
                "EMS",
                "Analgésicos",
                "Antitérmicos",
                "Genérico",
                new BigDecimal("12.90"),
                new BigDecimal("7.80"),
                60,
                Map.of("Loja 2", 40),
                false,
                LocalDate.of(2026, 6, 30),
                "https://dummyimage.com/300x300/E24A4A/ffffff&text=Paracetamol+750mg",
                null,
                null,
                false,
                null,
                false,
                "https://example.com/bulas/paracetamol.pdf",
                null,
                LocalDate.of(2025, 12, 15),
                "À vista",
                false,
                false
        );
    }

    @Test
    @DisplayName("GET /api/v1/products - Deve retornar lista de todos os produtos")
    void deveRetornarListaDeTodosProdutos() throws Exception {
        // Arrange
        List<ProductResponse> products = Arrays.asList(productResponse1, productResponse2);
        when(service.listarProdutos()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].nome", is("Dipirona Sódica 500mg")))
                .andExpect(jsonPath("$[0].laboratorio", is("Medley")))
                .andExpect(jsonPath("$[0].preco", is(8.50)))
                .andExpect(jsonPath("$[0].temPromocao", is(true)))
                .andExpect(jsonPath("$[1].id", is("2")))
                .andExpect(jsonPath("$[1].nome", is("Paracetamol 750mg")));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} - Deve retornar produto por ID")
    void deveRetornarProdutoPorId() throws Exception {
        // Arrange
        when(service.buscarPorId("1")).thenReturn(productResponse1);

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.nome", is("Dipirona Sódica 500mg")))
                .andExpect(jsonPath("$.laboratorio", is("Medley")))
                .andExpect(jsonPath("$.grupo", is("Analgésicos")))
                .andExpect(jsonPath("$.subGrupo", is("Antitérmicos")))
                .andExpect(jsonPath("$.linha", is("Genérico")))
                .andExpect(jsonPath("$.preco", is(8.50)))
                .andExpect(jsonPath("$.custo", is(5.20)))
                .andExpect(jsonPath("$.estoqueLojaAtual", is(45)))
                .andExpect(jsonPath("$.temPromocao", is(true)))
                .andExpect(jsonPath("$.vencimento", is("2025-12-31")))
                .andExpect(jsonPath("$.imagemUrl", is("https://dummyimage.com/300x300/4A90E2/ffffff&text=Dipirona+500mg")));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} - Deve retornar 404 quando produto não existir")
    void deveRetornar404QuandoProdutoNaoExistir() throws Exception {
        // Arrange
        when(service.buscarPorId("999")).thenThrow(new ResourceNotFoundException("Produto não encontrado com id: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
