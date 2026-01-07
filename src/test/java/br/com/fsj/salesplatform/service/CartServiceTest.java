package br.com.fsj.salesplatform.service;

import br.com.fsj.salesplatform.dto.CartResponse;
import br.com.fsj.salesplatform.dto.CreateCartRequest;
import br.com.fsj.salesplatform.exception.BusinessException;
import br.com.fsj.salesplatform.exception.ResourceNotFoundException;
import br.com.fsj.salesplatform.mapper.CartMapper;
import br.com.fsj.salesplatform.model.Cart;
import br.com.fsj.salesplatform.model.CartChannel;
import br.com.fsj.salesplatform.model.CartStatus;
import br.com.fsj.salesplatform.repository.CartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para CartService.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CartService Tests")
class CartServiceTest {
    
    @Mock
    private CartRepository cartRepository;
    
    @Mock
    private SseEmitterService sseEmitterService;
    
    @Mock
    private CartMapper cartMapper;
    
    @InjectMocks
    private CartService cartService;
    
    @Test
    @DisplayName("Deve criar novo carrinho quando cliente não tiver carrinho aberto")
    void createCart_deveCriarNovoCarrinho_quandoClienteNaoTiverCarrinhoAberto() {
        // Arrange
        String customerCpf = "12345678901";
        UUID cartId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        CreateCartRequest request = new CreateCartRequest(
                cartId.toString(), 
                customerCpf, 
                CartChannel.WEB
        );
        
        when(cartRepository.findByCustomerCpfAndStatus(customerCpf, CartStatus.OPEN))
                .thenReturn(Optional.empty());
        
        Cart cart = new Cart(cartId, customerCpf, CartChannel.WEB);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        
        CartResponse mockResponse = new CartResponse(
                cartId.toString(), 
                customerCpf, 
                CartChannel.WEB, 
                CartStatus.OPEN, 
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                0, 
                java.util.Collections.emptyList(),
                null, 
                null
        );
        when(cartMapper.toCartResponse(any(Cart.class))).thenReturn(mockResponse);
        
        // Act
        var response = cartService.createCart(request);
        
        // Assert
        assertNotNull(response);
        verify(cartRepository).save(any(Cart.class));
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao buscar carrinho inexistente")
    void getCartById_deveLancarExcecao_quandoCarrinhoNaoExistir() {
        // Arrange
        UUID cartId = UUID.fromString("660e8400-e29b-41d4-a716-446655440000");
        when(cartRepository.findByIdWithItems(cartId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> cartService.getCartById(cartId));
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao finalizar carrinho não aberto")
    void completeCart_deveLancarExcecao_quandoCarrinhoNaoEstiverAberto() {
        // Arrange
        UUID cartId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        String customerCpf = "12345678901";
        Cart cart = new Cart(cartId, customerCpf, CartChannel.WEB);
        cart.setStatus(CartStatus.COMPLETED);
        
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        
        // Act & Assert
        assertThrows(BusinessException.class, () -> cartService.completeCart(cartId));
    }
}

