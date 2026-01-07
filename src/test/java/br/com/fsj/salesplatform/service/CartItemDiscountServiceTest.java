package br.com.fsj.salesplatform.service;

import br.com.fsj.salesplatform.dto.AddDiscountRequest;
import br.com.fsj.salesplatform.dto.CartItemDiscountDTO;
import br.com.fsj.salesplatform.dto.UpdateDiscountRequest;
import br.com.fsj.salesplatform.exception.BusinessException;
import br.com.fsj.salesplatform.exception.ResourceNotFoundException;
import br.com.fsj.salesplatform.mapper.CartItemDiscountMapper;
import br.com.fsj.salesplatform.model.*;
import br.com.fsj.salesplatform.repository.CartItemDiscountRepository;
import br.com.fsj.salesplatform.repository.CartItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartItemDiscountService - Testes Unitários")
class CartItemDiscountServiceTest {

    @Mock
    private CartItemDiscountRepository discountRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartItemDiscountMapper mapper;

    @InjectMocks
    private CartItemDiscountService service;

    private Cart cart;
    private CartItem cartItem;
    private CartItemDiscount discount;
    private AddDiscountRequest addRequest;
    private CartItemDiscountDTO discountDTO;

    @BeforeEach
    void setUp() {
        UUID cartId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID cartItemId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");
        UUID discountId = UUID.fromString("770e8400-e29b-41d4-a716-446655440000");
        Long productId = 100L;

        cart = new Cart();
        cart.setId(cartId);
        cart.setCustomerCpf("12345678900");
        cart.setChannel(br.com.fsj.salesplatform.model.CartChannel.WEB);

        cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setCart(cart);
        cartItem.setProductId(productId);
        cartItem.setProductName("Produto Teste");
        cartItem.setUnitPrice(new BigDecimal("100.00"));
        cartItem.setQuantity(2);
        cartItem.setSubtotal(new BigDecimal("200.00"));

        discount = new CartItemDiscount();
        discount.setId(discountId);
        discount.setCartItem(cartItem);
        discount.setType(DiscountType.PROMOCAO);
        discount.setBaseAmount(new BigDecimal("200.00"));
        discount.setDiscountPercentage(new BigDecimal("10.00"));
        discount.setDiscountAmount(new BigDecimal("20.00"));
        discount.setCreatedAt(LocalDateTime.now());
        discount.setCreatedBy("admin");

        addRequest = new AddDiscountRequest(
                discountId.toString(),
                DiscountType.PROMOCAO,
                new BigDecimal("20.00"),
                "admin");

        discountDTO = new CartItemDiscountDTO(
                discountId.toString(),
                cartItemId.toString(),
                DiscountType.PROMOCAO,
                new BigDecimal("200.00"),
                new BigDecimal("10.00"),
                new BigDecimal("20.00"),
                LocalDateTime.now(),
                "admin",
                null,
                null);
    }

    @Test
    @DisplayName("Deve adicionar desconto com sucesso")
    void deveAdicionarDescontoComSucesso() {
        UUID cartId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID cartItemId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");

        cartItem.setTotalDiscount(BigDecimal.ZERO); // Nenhum desconto atual
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        when(mapper.toEntity(addRequest)).thenReturn(discount);
        when(discountRepository.save(any(CartItemDiscount.class))).thenReturn(discount);
        when(mapper.toDTO(discount)).thenReturn(discountDTO);

        CartItemDiscountDTO result = service.addDiscount(cartId, cartItemId, addRequest);

        assertNotNull(result);
        assertEquals(DiscountType.PROMOCAO, result.type());
        assertEquals(new BigDecimal("20.00"), result.discountAmount());
        verify(discountRepository).save(any(CartItemDiscount.class));
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    @DisplayName("Deve lançar exceção quando item não encontrado")
    void deveLancarExcecaoQuandoItemNaoEncontrado() {
        UUID cartId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID cartItemId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.addDiscount(cartId, cartItemId, addRequest));

        verify(discountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando item não pertence ao carrinho")
    void deveLancarExcecaoQuandoItemNaoPertenceAoCarrinho() {
        UUID cartId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID cartItemId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");
        UUID outroCarrinhoId = UUID.fromString("990e8400-e29b-41d4-a716-446655440000");

        Cart outroCarrinho = new Cart();
        outroCarrinho.setId(outroCarrinhoId);
        cartItem.setCart(outroCarrinho);

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));

        assertThrows(BusinessException.class,
                () -> service.addDiscount(cartId, cartItemId, addRequest));

        verify(discountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando desconto excede valor do item")
    void deveLancarExcecaoQuandoDescontoExcedeValorDoItem() {
        UUID cartId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID cartItemId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");
        UUID discountId = UUID.fromString("770e8400-e29b-41d4-a716-446655440001");

        AddDiscountRequest requestExcessivo = new AddDiscountRequest(
                discountId.toString(),
                DiscountType.BALCAO,
                new BigDecimal("250.00"), // Maior que subtotal de 200.00
                "admin");

        CartItemDiscount discountExcessivo = new CartItemDiscount();
        discountExcessivo.setId(discountId);
        discountExcessivo.setType(DiscountType.BALCAO);
        discountExcessivo.setBaseAmount(new BigDecimal("200.00"));
        discountExcessivo.setDiscountAmount(new BigDecimal("250.00"));

        cartItem.setTotalDiscount(BigDecimal.ZERO); // Nenhum desconto atual
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        when(mapper.toEntity(requestExcessivo)).thenReturn(discountExcessivo);

        assertThrows(BusinessException.class,
                () -> service.addDiscount(cartId, cartItemId, requestExcessivo));

        verify(discountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar descontos de um item")
    void deveListarDescontosDeUmItem() {
        UUID cartId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID cartItemId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");

        List<CartItemDiscount> discounts = Arrays.asList(discount);
        List<CartItemDiscountDTO> dtos = Arrays.asList(discountDTO);

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        when(discountRepository.findByCartItemId(cartItemId)).thenReturn(discounts);
        when(mapper.toDTOList(discounts)).thenReturn(dtos);

        List<CartItemDiscountDTO> result = service.listDiscounts(cartId, cartItemId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(DiscountType.PROMOCAO, result.get(0).type());
    }

    @Test
    @DisplayName("Deve buscar desconto específico")
    void deveBuscarDescontoEspecifico() {
        UUID cartId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID cartItemId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");
        UUID discountId = UUID.fromString("770e8400-e29b-41d4-a716-446655440000");

        when(discountRepository.findById(discountId)).thenReturn(Optional.of(discount));
        when(mapper.toDTO(discount)).thenReturn(discountDTO);

        CartItemDiscountDTO result = service.getDiscount(cartId, cartItemId, discountId);

        assertNotNull(result);
        assertEquals(discountId.toString(), result.id());
        assertEquals(DiscountType.PROMOCAO, result.type());
    }

    @Test
    @DisplayName("Deve atualizar valor do desconto")
    void deveAtualizarValorDoDesconto() {
        UUID cartId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID cartItemId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");
        UUID discountId = UUID.fromString("770e8400-e29b-41d4-a716-446655440000");

        UpdateDiscountRequest updateRequest = new UpdateDiscountRequest(
                new BigDecimal("30.00"),
                "manager");

        when(discountRepository.findById(discountId)).thenReturn(Optional.of(discount));
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        cartItem.setTotalDiscount(new BigDecimal("20.00")); // Simular total atual
        when(discountRepository.save(discount)).thenReturn(discount);
        when(mapper.toDTO(discount)).thenReturn(discountDTO);

        CartItemDiscountDTO result = service.updateDiscount(cartId, cartItemId, discountId, updateRequest);

        assertNotNull(result);
        verify(discountRepository).save(discount);
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    @DisplayName("Deve remover desconto")
    void deveRemoverDesconto() {
        UUID cartId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID cartItemId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");
        UUID discountId = UUID.fromString("770e8400-e29b-41d4-a716-446655440000");

        when(discountRepository.findById(discountId)).thenReturn(Optional.of(discount));

        service.removeDiscount(cartId, cartItemId, discountId);

        verify(discountRepository).delete(discount);
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    @DisplayName("Deve remover todos os descontos de um item")
    void deveRemoverTodosOsDescontosDeUmItem() {
        UUID cartId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID cartItemId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));

        service.removeAllDiscounts(cartId, cartItemId);

        verify(discountRepository).deleteByCartItemId(cartItemId);
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    @DisplayName("Deve remover descontos por tipo")
    void deveRemoverDescontosPorTipo() {
        UUID cartId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID cartItemId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));

        service.removeDiscountsByType(cartId, cartItemId, DiscountType.PROMOCAO);

        verify(discountRepository).deleteByCartItemIdAndType(cartItemId, DiscountType.PROMOCAO);
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    @DisplayName("Deve calcular total de descontos")
    void deveCalcularTotalDeDescontos() {
        UUID cartItemId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");

        BigDecimal expectedTotal = new BigDecimal("50.00");
        cartItem.setTotalDiscount(expectedTotal);
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));

        BigDecimal total = service.getTotalDiscount(cartItemId);

        assertEquals(expectedTotal, total);
    }

    @Test
    @DisplayName("Deve validar múltiplos descontos não excedem valor do item")
    void deveValidarMultiplosDescontosNaoExcedemValorDoItem() {
        UUID cartId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID cartItemId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");
        UUID discountId = UUID.fromString("770e8400-e29b-41d4-a716-446655440002");

        // Já existe desconto de 150.00
        cartItem.setTotalDiscount(new BigDecimal("150.00"));
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));

        AddDiscountRequest novoDesconto = new AddDiscountRequest(
                discountId.toString(),
                DiscountType.BALCAO,
                new BigDecimal("60.00"), // 150 + 60 = 210 > 200 (subtotal)
                "admin");

        CartItemDiscount novoDescontoEntity = new CartItemDiscount();
        novoDescontoEntity.setId(discountId);
        novoDescontoEntity.setType(DiscountType.BALCAO);
        novoDescontoEntity.setBaseAmount(new BigDecimal("200.00"));
        novoDescontoEntity.setDiscountAmount(new BigDecimal("60.00"));

        when(mapper.toEntity(novoDesconto)).thenReturn(novoDescontoEntity);

        assertThrows(BusinessException.class,
                () -> service.addDiscount(cartId, cartItemId, novoDesconto));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar adicionar desconto duplicado do mesmo tipo")
    void deveLancarExcecaoAoTentarAdicionarDescontoDuplicadoDoMesmoTipo() {
        UUID cartId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID cartItemId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");
        UUID discountId = UUID.fromString("770e8400-e29b-41d4-a716-446655440003");

        // Arrange
        cartItem.setTotalDiscount(BigDecimal.ZERO);
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        when(discountRepository.existsByCartItemIdAndType(cartItemId, DiscountType.PROMOCAO)).thenReturn(true);

        AddDiscountRequest duplicateDiscount = new AddDiscountRequest(
                discountId.toString(),
                DiscountType.PROMOCAO,
                new BigDecimal("10.00"),
                "admin");

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.addDiscount(cartId, cartItemId, duplicateDiscount));

        assertTrue(exception.getMessage().contains("Já existe um desconto do tipo PROMOCAO"));
        verify(discountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar total_discount ao adicionar desconto")
    void deveAtualizarTotalDiscountAoAdicionarDesconto() {
        // Arrange
        UUID cartId = cart.getId();
        UUID cartItemId = cartItem.getId();
        
        // Item sem descontos inicialmente
        cartItem.setTotalDiscount(BigDecimal.ZERO);
        
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        when(mapper.toEntity(addRequest)).thenReturn(discount);
        when(discountRepository.save(any(CartItemDiscount.class))).thenReturn(discount);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> {
            CartItem savedItem = invocation.getArgument(0);
            // Verificar que o total_discount foi recalculado
            assertEquals(new BigDecimal("20.00"), savedItem.getTotalDiscount());
            return savedItem;
        });
        when(mapper.toDTO(discount)).thenReturn(discountDTO);

        // Act
        service.addDiscount(cartId, cartItemId, addRequest);

        // Assert
        verify(cartItemRepository).save(argThat(item -> 
            item.getTotalDiscount().compareTo(new BigDecimal("20.00")) == 0
        ));
    }

    @Test
    @DisplayName("Deve atualizar total_discount ao adicionar múltiplos descontos")
    void deveAtualizarTotalDiscountAoAdicionarMultiplosDescontos() {
        // Arrange
        UUID cartId = cart.getId();
        UUID cartItemId = cartItem.getId();
        
        // Primeiro desconto já existe
        CartItemDiscount primeiroDesconto = new CartItemDiscount();
        primeiroDesconto.setId(UUID.randomUUID());
        primeiroDesconto.setCartItem(cartItem);
        primeiroDesconto.setType(DiscountType.BALCAO);
        primeiroDesconto.setDiscountAmount(new BigDecimal("10.00"));
        
        cartItem.getDiscounts().add(primeiroDesconto);
        cartItem.setTotalDiscount(new BigDecimal("10.00"));
        
        // Segundo desconto a ser adicionado
        CartItemDiscount segundoDesconto = new CartItemDiscount();
        segundoDesconto.setId(UUID.randomUUID());
        segundoDesconto.setType(DiscountType.PROMOCAO);
        segundoDesconto.setDiscountAmount(new BigDecimal("15.00"));
        
        AddDiscountRequest request = new AddDiscountRequest(
            segundoDesconto.getId().toString(),
            DiscountType.PROMOCAO,
            new BigDecimal("15.00"),
            "admin"
        );
        
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        when(mapper.toEntity(request)).thenReturn(segundoDesconto);
        when(discountRepository.save(any(CartItemDiscount.class))).thenReturn(segundoDesconto);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> {
            CartItem savedItem = invocation.getArgument(0);
            // Verificar que o total_discount foi recalculado (10 + 15 = 25)
            assertEquals(new BigDecimal("25.00"), savedItem.getTotalDiscount());
            return savedItem;
        });
        when(mapper.toDTO(segundoDesconto)).thenReturn(
            new CartItemDiscountDTO(
                segundoDesconto.getId().toString(),
                cartItemId.toString(),
                DiscountType.PROMOCAO,
                new BigDecimal("200.00"),
                new BigDecimal("7.50"),
                new BigDecimal("15.00"),
                LocalDateTime.now(),
                "admin",
                null,
                null
            )
        );

        // Act
        service.addDiscount(cartId, cartItemId, request);

        // Assert
        verify(cartItemRepository).save(argThat(item -> 
            item.getTotalDiscount().compareTo(new BigDecimal("25.00")) == 0
        ));
    }

    @Test
    @DisplayName("Deve atualizar total_discount ao atualizar desconto")
    void deveAtualizarTotalDiscountAoAtualizarDesconto() {
        // Arrange
        UUID cartId = cart.getId();
        UUID cartItemId = cartItem.getId();
        UUID discountId = discount.getId();
        
        // Configurar o relacionamento bidirecional corretamente
        discount.setCartItem(cartItem);
        cartItem.getDiscounts().add(discount);
        cartItem.setTotalDiscount(new BigDecimal("20.00"));
        
        // Atualizar para novo valor
        UpdateDiscountRequest updateRequest = new UpdateDiscountRequest(
            new BigDecimal("30.00"),
            "admin"
        );
        
        // Mock para buscar o desconto
        when(discountRepository.findById(discountId)).thenReturn(Optional.of(discount));
        
        // Mock para buscar o item (usado em getTotalDiscount dentro de validateDiscountValueForUpdate)
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        
        when(discountRepository.save(any(CartItemDiscount.class))).thenAnswer(invocation -> {
            CartItemDiscount updated = invocation.getArgument(0);
            updated.setDiscountAmount(new BigDecimal("30.00"));
            return updated;
        });
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> {
            CartItem savedItem = invocation.getArgument(0);
            // Verificar que o total_discount foi recalculado para 30
            assertEquals(new BigDecimal("30.00"), savedItem.getTotalDiscount());
            return savedItem;
        });
        when(mapper.toDTO(any(CartItemDiscount.class))).thenReturn(discountDTO);

        // Act
        service.updateDiscount(cartId, cartItemId, discountId, updateRequest);

        // Assert
        verify(cartItemRepository).save(argThat(item -> 
            item.getTotalDiscount().compareTo(new BigDecimal("30.00")) == 0
        ));
    }

    @Test
    @DisplayName("Deve atualizar total_discount ao remover desconto")
    void deveAtualizarTotalDiscountAoRemoverDesconto() {
        // Arrange
        UUID cartId = cart.getId();
        UUID cartItemId = cartItem.getId();
        UUID discountId = discount.getId();
        
        // Dois descontos existentes
        CartItemDiscount primeiroDesconto = new CartItemDiscount();
        primeiroDesconto.setId(discountId);
        primeiroDesconto.setCartItem(cartItem);
        primeiroDesconto.setType(DiscountType.PROMOCAO);
        primeiroDesconto.setDiscountAmount(new BigDecimal("20.00"));
        
        CartItemDiscount segundoDesconto = new CartItemDiscount();
        segundoDesconto.setId(UUID.randomUUID());
        segundoDesconto.setCartItem(cartItem);
        segundoDesconto.setType(DiscountType.BALCAO);
        segundoDesconto.setDiscountAmount(new BigDecimal("10.00"));
        
        cartItem.getDiscounts().add(primeiroDesconto);
        cartItem.getDiscounts().add(segundoDesconto);
        cartItem.setTotalDiscount(new BigDecimal("30.00"));
        
        when(discountRepository.findById(discountId)).thenReturn(Optional.of(primeiroDesconto));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> {
            CartItem savedItem = invocation.getArgument(0);
            // Após remover o primeiro desconto, deve restar apenas 10
            assertEquals(new BigDecimal("10.00"), savedItem.getTotalDiscount());
            return savedItem;
        });

        // Act
        service.removeDiscount(cartId, cartItemId, discountId);

        // Assert
        verify(discountRepository).delete(primeiroDesconto);
        verify(cartItemRepository).save(argThat(item -> 
            item.getTotalDiscount().compareTo(new BigDecimal("10.00")) == 0
        ));
    }

    @Test
    @DisplayName("Deve zerar total_discount ao remover todos os descontos")
    void deveZerarTotalDiscountAoRemoverTodosDescontos() {
        // Arrange
        UUID cartId = cart.getId();
        UUID cartItemId = cartItem.getId();
        
        // Múltiplos descontos existentes
        CartItemDiscount desconto1 = new CartItemDiscount();
        desconto1.setDiscountAmount(new BigDecimal("20.00"));
        
        CartItemDiscount desconto2 = new CartItemDiscount();
        desconto2.setDiscountAmount(new BigDecimal("10.00"));
        
        cartItem.getDiscounts().add(desconto1);
        cartItem.getDiscounts().add(desconto2);
        cartItem.setTotalDiscount(new BigDecimal("30.00"));
        
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> {
            CartItem savedItem = invocation.getArgument(0);
            // Após remover todos, deve ser zero
            assertEquals(BigDecimal.ZERO, savedItem.getTotalDiscount());
            return savedItem;
        });

        // Act
        service.removeAllDiscounts(cartId, cartItemId);

        // Assert
        verify(discountRepository).deleteByCartItemId(cartItemId);
        verify(cartItemRepository).save(argThat(item -> 
            item.getTotalDiscount().compareTo(BigDecimal.ZERO) == 0
        ));
    }
}
