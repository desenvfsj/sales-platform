package br.com.fsj.salesplatform.service;

import br.com.fsj.salesplatform.dto.ProductResponse;
import br.com.fsj.salesplatform.exception.ResourceNotFoundException;
import br.com.fsj.salesplatform.mapper.ProductMapper;
import br.com.fsj.salesplatform.model.Product;
import br.com.fsj.salesplatform.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ProductService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService - Testes Unitários")
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService service;

    private Product product1;
    private Product product2;
    private ProductResponse productResponse1;
    private ProductResponse productResponse2;
    
    private Product createProduct(String id, String nome, String laboratorio, String grupo,
                                  String subGrupo, String linha, BigDecimal preco, BigDecimal custo,
                                  Integer estoqueLojaAtual, Map<String, Integer> estoqueOutrasLojas,
                                  Boolean temPromocao, LocalDate vencimento, String imagemUrl) {
        Product product = new Product();
        product.setId(id);
        product.setNome(nome);
        product.setLaboratorio(laboratorio);
        product.setGrupo(grupo);
        product.setSubGrupo(subGrupo);
        product.setLinha(linha);
        product.setPreco(preco);
        product.setCusto(custo);
        product.setEstoqueLojaAtual(estoqueLojaAtual);
        product.setEstoqueOutrasLojas(estoqueOutrasLojas);
        product.setTemPromocao(temPromocao);
        product.setVencimento(vencimento);
        product.setImagemUrl(imagemUrl);
        product.setNomePBM(nome + " - PBM");
        product.setMensagemDescontoPBM("Desconto de 40% com PBM");
        product.setControlado(false);
        product.setDetalhamentoCorReceita("Receita Branca Simples");
        product.setAntibiotico(false);
        product.setBula("https://example.com/bulas/" + nome.toLowerCase().split(" ")[0] + ".pdf");
        product.setAlcada(null);
        product.setUltimaEntradaNota(LocalDate.of(2025, 12, 20));
        product.setPlanoPagamento("À vista, 2x, 3x sem juros");
        product.setRuptura(false);
        product.setExcesso(false);
        return product;
    }

    @BeforeEach
    void setUp() {
        // Setup de produtos mockados
        product1 = createProduct(
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
                "https://dummyimage.com/300x300/4A90E2/ffffff&text=Dipirona+500mg"
        );

        product2 = createProduct(
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
                "https://dummyimage.com/300x300/E24A4A/ffffff&text=Paracetamol+750mg"
        );
        product2.setNomePBM(null);
        product2.setMensagemDescontoPBM(null);
        product2.setDetalhamentoCorReceita(null);
        product2.setUltimaEntradaNota(LocalDate.of(2025, 12, 15));
        product2.setPlanoPagamento("À vista");

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
    @DisplayName("Deve listar todos os produtos")
    void deveListarTodosProdutos() {
        // Arrange
        List<Product> products = Arrays.asList(product1, product2);

        when(repository.findAll()).thenReturn(products);
        when(productMapper.toProductResponse(product1)).thenReturn(productResponse1);
        when(productMapper.toProductResponse(product2)).thenReturn(productResponse2);

        // Act
        List<ProductResponse> result = service.listarProdutos();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Dipirona Sódica 500mg", result.get(0).nome());
        assertEquals("Paracetamol 750mg", result.get(1).nome());
        assertEquals("Medley", result.get(0).laboratorio());
        assertEquals("EMS", result.get(1).laboratorio());

        verify(repository, times(1)).findAll();
        verify(productMapper, times(2)).toProductResponse(any(Product.class));
    }

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void deveBuscarProdutoPorIdComSucesso() {
        // Arrange
        when(repository.findById("1")).thenReturn(Optional.of(product1));
        when(productMapper.toProductResponse(product1)).thenReturn(productResponse1);

        // Act
        ProductResponse result = service.buscarPorId("1");

        // Assert
        assertNotNull(result);
        assertEquals("1", result.id());
        assertEquals("Dipirona Sódica 500mg", result.nome());
        assertEquals("Medley", result.laboratorio());
        assertEquals("Analgésicos", result.grupo());
        assertEquals(new BigDecimal("8.50"), result.preco());

        verify(repository, times(1)).findById("1");
        verify(productMapper, times(1)).toProductResponse(product1);
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não for encontrado")
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        // Arrange
        when(repository.findById("999")).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.buscarPorId("999")
        );

        assertEquals("Produto não encontrado com id: 999", exception.getMessage());

        verify(repository, times(1)).findById("999");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver produtos")
    void deveRetornarListaVaziaQuandoNaoHouverProdutos() {
        // Arrange
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<ProductResponse> result = service.listarProdutos();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository, times(1)).findAll();
    }
}
