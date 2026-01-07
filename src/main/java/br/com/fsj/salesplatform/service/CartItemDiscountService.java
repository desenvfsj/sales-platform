package br.com.fsj.salesplatform.service;

import br.com.fsj.salesplatform.dto.AddDiscountRequest;
import br.com.fsj.salesplatform.dto.CartItemDiscountDTO;
import br.com.fsj.salesplatform.dto.UpdateDiscountRequest;
import br.com.fsj.salesplatform.exception.BusinessException;
import br.com.fsj.salesplatform.exception.ResourceNotFoundException;
import br.com.fsj.salesplatform.mapper.CartItemDiscountMapper;
import br.com.fsj.salesplatform.model.CartItem;
import br.com.fsj.salesplatform.model.CartItemDiscount;
import br.com.fsj.salesplatform.model.DiscountType;
import br.com.fsj.salesplatform.repository.CartItemDiscountRepository;
import br.com.fsj.salesplatform.repository.CartItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Service para gerenciar descontos de itens do carrinho.
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
@Service
public class CartItemDiscountService {

    private static final Logger log = LoggerFactory.getLogger(CartItemDiscountService.class);

    private final CartItemDiscountRepository discountRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemDiscountMapper mapper;

    /**
     * Construtor com injeção de dependências.
     */
    public CartItemDiscountService(CartItemDiscountRepository discountRepository,
            CartItemRepository cartItemRepository,
            CartItemDiscountMapper mapper) {
        this.discountRepository = discountRepository;
        this.cartItemRepository = cartItemRepository;
        this.mapper = mapper;
    }

    /**
     * Adiciona um desconto a um item do carrinho.
     *
     * <p>
     * Os campos baseAmount e discountPercentage são calculados automaticamente:
     * </p>
     * <ul>
     * <li>baseAmount: obtido do subtotal do item (quantidade * preço unitário)</li>
     * <li>discountPercentage: calculado como (discountAmount / baseAmount) *
     * 100</li>
     * </ul>
     *
     * @param cartId  ID do carrinho
     * @param itemId  ID do item
     * @param request Dados do desconto (apenas discountAmount é enviado pelo
     *                cliente)
     * @return DTO do desconto criado
     */
    @Transactional
    public CartItemDiscountDTO addDiscount(UUID cartId, UUID itemId, AddDiscountRequest request) {
        log.info("Adicionando desconto tipo {} ao item {} do carrinho {}", request.type(), itemId, cartId);

        CartItem cartItem = findCartItem(cartId, itemId);

        // Validar que não existe desconto do mesmo tipo
        if (discountRepository.existsByCartItemIdAndType(itemId, request.type())) {
            throw new BusinessException(
                    String.format("Já existe um desconto do tipo %s para este item. " +
                            "Remova o desconto existente ou atualize seu valor.", request.type()));
        }

        CartItemDiscount discount = mapper.toEntity(request);
        discount.setCartItem(cartItem);

        // Calcular baseAmount e discountPercentage automaticamente
        BigDecimal baseAmount = cartItem.getSubtotal();
        discount.setBaseAmount(baseAmount);

        // Calcular o percentual: (discountAmount / baseAmount) * 100
        if (baseAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentage = request.discountAmount()
                    .divide(baseAmount, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
            discount.setDiscountPercentage(percentage);
        } else {
            discount.setDiscountPercentage(BigDecimal.ZERO);
        }

        // Validar que o desconto não excede o valor do item
        validateDiscountValue(cartItem, discount.getDiscountAmount());

        // Salvar o desconto (o Hibernate gerenciará a relação automaticamente)
        CartItemDiscount saved = discountRepository.save(discount);

        // Adicionar o desconto salvo à lista do item para recalcular corretamente
        // Importante: usar o desconto retornado pelo save, não o original
        if (!cartItem.getDiscounts().contains(saved)) {
            cartItem.getDiscounts().add(saved);
        }

        // Recalcular e atualizar o total_discount do item
        cartItem.recalculateTotalDiscount();
        cartItem.setUpdatedAt(java.time.LocalDateTime.now());
        cartItemRepository.save(cartItem);

        log.info("Desconto {} adicionado com sucesso ao item {} (baseAmount: {}, percentage: {}%, total_discount: {})",
                saved.getId(), itemId, baseAmount, discount.getDiscountPercentage(), cartItem.getTotalDiscount());
        return mapper.toDTO(saved);
    }

    /**
     * Lista todos os descontos de um item.
     *
     * @param cartId ID do carrinho
     * @param itemId ID do item
     * @return Lista de descontos
     */
    @Transactional(readOnly = true)
    public List<CartItemDiscountDTO> listDiscounts(UUID cartId, UUID itemId) {
        log.debug("Listando descontos do item {} do carrinho {}", itemId, cartId);

        // Validar que o item existe e pertence ao carrinho
        findCartItem(cartId, itemId);

        List<CartItemDiscount> discounts = discountRepository.findByCartItemId(itemId);
        return mapper.toDTOList(discounts);
    }

    /**
     * Busca um desconto específico.
     *
     * @param cartId     ID do carrinho
     * @param itemId     ID do item
     * @param discountId ID do desconto
     * @return DTO do desconto
     */
    @Transactional(readOnly = true)
    public CartItemDiscountDTO getDiscount(UUID cartId, UUID itemId, UUID discountId) {
        log.debug("Buscando desconto {} do item {} do carrinho {}", discountId, itemId, cartId);

        CartItemDiscount discount = findDiscount(cartId, itemId, discountId);
        return mapper.toDTO(discount);
    }

    /**
     * Atualiza o valor de um desconto.
     * 
     * <p>
     * Os campos baseAmount e discountPercentage são recalculados automaticamente:
     * </p>
     * <ul>
     * <li>baseAmount: obtido do subtotal atual do item</li>
     * <li>discountPercentage: recalculado como (discountAmount / baseAmount) *
     * 100</li>
     * </ul>
     *
     * @param cartId     ID do carrinho
     * @param itemId     ID do item
     * @param discountId ID do desconto
     * @param request    Novos dados do desconto (apenas discountAmount)
     * @return DTO do desconto atualizado
     */
    @Transactional
    public CartItemDiscountDTO updateDiscount(UUID cartId, UUID itemId, UUID discountId,
            UpdateDiscountRequest request) {
        log.info("Atualizando desconto {} do item {} do carrinho {}", discountId, itemId, cartId);

        CartItemDiscount discount = findDiscount(cartId, itemId, discountId);
        CartItem cartItem = discount.getCartItem();

        // Atualizar valor do desconto
        discount.setDiscountAmount(request.discountAmount());

        // Recalcular baseAmount e discountPercentage automaticamente
        BigDecimal baseAmount = cartItem.getSubtotal();
        discount.setBaseAmount(baseAmount);

        // Calcular o percentual: (discountAmount / baseAmount) * 100
        if (baseAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentage = request.discountAmount()
                    .divide(baseAmount, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
            discount.setDiscountPercentage(percentage);
        } else {
            discount.setDiscountPercentage(BigDecimal.ZERO);
        }

        // Validar novo valor
        validateDiscountValueForUpdate(cartItem, discount, discount.getDiscountAmount());

        discount.setUpdatedBy(request.updatedBy());
        discount.setUpdatedAt(java.time.LocalDateTime.now());

        CartItemDiscount updated = discountRepository.save(discount);

        // Recalcular e atualizar o total_discount do item
        cartItem.recalculateTotalDiscount();
        cartItem.setUpdatedAt(java.time.LocalDateTime.now());
        cartItemRepository.save(cartItem);

        log.info("Desconto {} atualizado com sucesso (novo valor: {}, percentage: {}%, total_discount: {})",
                discountId, request.discountAmount(), discount.getDiscountPercentage(), cartItem.getTotalDiscount());
        return mapper.toDTO(updated);
    }

    /**
     * Remove um desconto de um item.
     *
     * @param cartId     ID do carrinho
     * @param itemId     ID do item
     * @param discountId ID do desconto
     */
    @Transactional
    public void removeDiscount(UUID cartId, UUID itemId, UUID discountId) {
        log.info("Removendo desconto {} do item {} do carrinho {}", discountId, itemId, cartId);

        CartItemDiscount discount = findDiscount(cartId, itemId, discountId);
        CartItem cartItem = discount.getCartItem();

        // Remover da lista do item
        cartItem.getDiscounts().remove(discount);

        // Deletar do banco de dados
        discountRepository.delete(discount);

        // Recalcular e atualizar o total_discount do item
        cartItem.recalculateTotalDiscount();
        cartItem.setUpdatedAt(java.time.LocalDateTime.now());
        cartItemRepository.save(cartItem);

        log.info("Desconto {} removido com sucesso (total_discount atualizado: {})",
                discountId, cartItem.getTotalDiscount());
    }

    /**
     * Remove todos os descontos de um item.
     *
     * @param cartId ID do carrinho
     * @param itemId ID do item
     */
    @Transactional
    public void removeAllDiscounts(UUID cartId, UUID itemId) {
        log.info("Removendo todos os descontos do item {} do carrinho {}", itemId, cartId);

        CartItem cartItem = findCartItem(cartId, itemId);

        // Limpar a lista de descontos do item
        cartItem.getDiscounts().clear();

        // Deletar do banco de dados
        discountRepository.deleteByCartItemId(itemId);

        // Recalcular e atualizar o total_discount do item (será zero)
        cartItem.recalculateTotalDiscount();
        cartItem.setUpdatedAt(java.time.LocalDateTime.now());
        cartItemRepository.save(cartItem);

        log.info("Todos os descontos do item {} removidos com sucesso", itemId);
    }

    /**
     * Remove todos os descontos de um tipo específico de um item.
     *
     * @param cartId ID do carrinho
     * @param itemId ID do item
     * @param type   Tipo do desconto
     */
    @Transactional
    public void removeDiscountsByType(UUID cartId, UUID itemId, DiscountType type) {
        log.info("Removendo descontos tipo {} do item {} do carrinho {}", type, itemId, cartId);

        CartItem cartItem = findCartItem(cartId, itemId);

        // Remover descontos do tipo especificado da lista do item
        cartItem.getDiscounts().removeIf(discount -> discount.getType() == type);

        // Deletar do banco de dados
        discountRepository.deleteByCartItemIdAndType(itemId, type);

        // Recalcular e atualizar o total_discount do item
        cartItem.recalculateTotalDiscount();
        cartItem.setUpdatedAt(java.time.LocalDateTime.now());
        cartItemRepository.save(cartItem);

        log.info("Descontos tipo {} do item {} removidos com sucesso", type, itemId);
    }

    /**
     * Obtém o total de descontos de um item (campo persistido).
     *
     * @param itemId ID do item
     * @return Total de descontos
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalDiscount(UUID itemId) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado com id: " + itemId));
        return cartItem.getTotalDiscount();
    }

    // Métodos auxiliares privados

    private CartItem findCartItem(UUID cartId, UUID itemId) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado com id: " + itemId));

        if (!cartItem.getCart().getId().equals(cartId)) {
            throw new BusinessException("Item não pertence ao carrinho informado");
        }

        return cartItem;
    }

    private CartItemDiscount findDiscount(UUID cartId, UUID itemId, UUID discountId) {
        CartItemDiscount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new ResourceNotFoundException("Desconto não encontrado com id: " + discountId));

        if (!discount.getCartItem().getId().equals(itemId)) {
            throw new BusinessException("Desconto não pertence ao item informado");
        }

        if (!discount.getCartItem().getCart().getId().equals(cartId)) {
            throw new BusinessException("Item não pertence ao carrinho informado");
        }

        return discount;
    }

    private void validateDiscountValue(CartItem cartItem, BigDecimal discountValue) {
        BigDecimal currentTotalDiscount = getTotalDiscount(cartItem.getId());
        BigDecimal newTotalDiscount = currentTotalDiscount.add(discountValue);
        BigDecimal itemSubtotal = cartItem.getSubtotal();

        if (newTotalDiscount.compareTo(itemSubtotal) > 0) {
            throw new BusinessException(
                    String.format("Total de descontos (R$ %.2f) não pode exceder o valor do item (R$ %.2f)",
                            newTotalDiscount, itemSubtotal));
        }
    }

    private void validateDiscountValueForUpdate(CartItem cartItem, CartItemDiscount currentDiscount,
            BigDecimal newValue) {
        BigDecimal currentTotalDiscount = getTotalDiscount(cartItem.getId());
        BigDecimal totalWithoutCurrent = currentTotalDiscount.subtract(currentDiscount.getDiscountAmount());
        BigDecimal newTotalDiscount = totalWithoutCurrent.add(newValue);
        BigDecimal itemSubtotal = cartItem.getSubtotal();

        if (newTotalDiscount.compareTo(itemSubtotal) > 0) {
            throw new BusinessException(
                    String.format("Total de descontos (R$ %.2f) não pode exceder o valor do item (R$ %.2f)",
                            newTotalDiscount, itemSubtotal));
        }
    }
}
