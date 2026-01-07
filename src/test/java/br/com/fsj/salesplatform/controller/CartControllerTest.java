package br.com.fsj.salesplatform.controller;

import br.com.fsj.salesplatform.dto.CartResponse;
import br.com.fsj.salesplatform.dto.CreateCartRequest;
import br.com.fsj.salesplatform.model.CartChannel;
import br.com.fsj.salesplatform.model.CartStatus;
import br.com.fsj.salesplatform.service.CartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Testes unitários para CartController.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CartController Tests")
class CartControllerTest {
    
    @Mock
    private CartService cartService;
    
    @InjectMocks
    private CartController cartController;
    
    @Test
    @DisplayName("Deve criar carrinho com sucesso")
    void createCart_deveRetornarCarrinhoCriado() {
        // Arrange
        String cartId = "550e8400-e29b-41d4-a716-446655440000";
        CreateCartRequest request = new CreateCartRequest(
                cartId, 
                "12345678901", 
                CartChannel.WEB
        );
        CartResponse mockResponse = new CartResponse(
                cartId, 
                "12345678901", 
                CartChannel.WEB, 
                CartStatus.OPEN,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                0, 
                new ArrayList<>(),
                LocalDateTime.now(), 
                LocalDateTime.now()
        );
        
        when(cartService.createCart(any(CreateCartRequest.class)))
                .thenReturn(mockResponse);
        
        // Act
        var response = cartController.createCart(request);
        
        // Assert
        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(CartStatus.OPEN, response.getBody().status());
    }
}

