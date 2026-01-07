package br.com.fsj.salesplatform.repository;

import br.com.fsj.salesplatform.model.Cart;
import br.com.fsj.salesplatform.model.CartItem;
import br.com.fsj.salesplatform.model.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para acesso a dados de carrinhos.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    
    /**
     * Busca carrinho de um cliente por CPF e status.
     * 
     * @param customerCpf CPF do cliente
     * @param status Status do carrinho
     * @return Optional com o carrinho encontrado
     */
    Optional<Cart> findByCustomerCpfAndStatus(String customerCpf, CartStatus status);
    
    /**
     * Busca carrinho por ID com itens carregados.
     * Os descontos serão carregados em uma segunda query.
     * 
     * @param id UUID do carrinho
     * @return Optional com o carrinho e seus itens
     */
    @Query("SELECT DISTINCT c FROM Cart c " +
           "LEFT JOIN FETCH c.items " +
           "WHERE c.id = :id")
    Optional<Cart> findByIdWithItems(@Param("id") UUID id);
    
    /**
     * Carrega os descontos dos itens de um carrinho.
     * Deve ser chamado após findByIdWithItems para evitar MultipleBagFetchException.
     * 
     * @param cartId UUID do carrinho
     */
    @Query("SELECT i FROM CartItem i " +
           "LEFT JOIN FETCH i.discounts " +
           "WHERE i.cart.id = :cartId")
    List<CartItem> findItemsWithDiscountsByCartId(@Param("cartId") UUID cartId);
    
    /**
     * Busca carrinho de um cliente por CPF e status com itens carregados.
     * 
     * @param customerCpf CPF do cliente
     * @param status Status do carrinho
     * @return Optional com o carrinho e seus itens
     */
    @Query("SELECT DISTINCT c FROM Cart c " +
           "LEFT JOIN FETCH c.items " +
           "WHERE c.customerCpf = :customerCpf AND c.status = :status")
    Optional<Cart> findByCustomerCpfAndStatusWithItems(
            @Param("customerCpf") String customerCpf, 
            @Param("status") CartStatus status);
    
    /**
     * Busca todos os carrinhos de um cliente por CPF.
     * 
     * @param customerCpf CPF do cliente
     * @return Lista de carrinhos
     */
    List<Cart> findByCustomerCpf(String customerCpf);
    
    /**
     * Busca carrinhos inativos (para processo de abandono).
     * 
     * @param status Status do carrinho
     * @param date Data limite de atualização
     * @return Lista de carrinhos inativos
     */
    List<Cart> findByStatusAndUpdatedAtBefore(CartStatus status, LocalDateTime date);
}

