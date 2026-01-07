package br.com.fsj.salesplatform.application.core.usecase;

import br.com.fsj.salesplatform.application.core.domain.Cart;
import br.com.fsj.salesplatform.application.core.domain.CartStatus;
import br.com.fsj.salesplatform.application.ports.in.CreateCartInputPort;
import br.com.fsj.salesplatform.application.ports.in.FindCartInputPort;
import br.com.fsj.salesplatform.application.ports.in.ManageCartInputPort;
import br.com.fsj.salesplatform.application.ports.out.CartEventOutputPort;
import br.com.fsj.salesplatform.application.ports.out.CartRepositoryOutputPort;
import br.com.fsj.salesplatform.application.core.exception.BusinessException;
import br.com.fsj.salesplatform.application.core.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CartUseCase implements CreateCartInputPort, FindCartInputPort, ManageCartInputPort {

    private final CartRepositoryOutputPort cartRepository;
    private final CartEventOutputPort cartEventPort;

    public CartUseCase(CartRepositoryOutputPort cartRepository, CartEventOutputPort cartEventPort) {
        this.cartRepository = cartRepository;
        this.cartEventPort = cartEventPort;
    }

    @Override
    @Transactional
    public Cart create(Cart cart) {
        var existingCart = cartRepository.findByCustomerCpfAndStatus(cart.getCustomerCpf(), CartStatus.OPEN);
        
        if (existingCart.isPresent()) {
            return existingCart.get();
        }

        Cart saved = cartRepository.save(cart);
        cartEventPort.sendCartUpdate(saved);
        return saved;
    }

    @Override
    public Cart findOpenByCpf(String cpf) {
        return cartRepository.findByCustomerCpfAndStatus(cpf, CartStatus.OPEN)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente CPF " + cpf + " não possui carrinho aberto"));
    }

    @Override
    public Cart findById(UUID id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado com id: " + id));
    }

    @Override
    public Cart findSummary(UUID id) {
        return findById(id);
    }

    @Override
    @Transactional
    public void clearCart(UUID cartId) {
        Cart cart = findById(cartId);
        
        if (!cart.isOpen()) {
            throw new BusinessException("Não é possível limpar carrinho com status: " + cart.getStatus());
        }
        
        cart.getItems().clear();
        cart.calculateTotal();
        
        Cart saved = cartRepository.save(cart);
        cartEventPort.sendCartUpdate(saved);
    }

    @Override
    @Transactional
    public Cart completeCart(UUID cartId) {
        Cart cart = findById(cartId);
        
        if (!cart.isOpen()) {
            throw new BusinessException("Não é possível finalizar carrinho com status: " + cart.getStatus());
        }
        
        if (cart.getItems().isEmpty()) {
            throw new BusinessException("Não é possível finalizar carrinho vazio");
        }
        
        cart.complete();
        Cart saved = cartRepository.save(cart);
        
        cartEventPort.sendCartCompleted(saved);
        return saved;
    }

    @Override
    @Transactional
    public Cart calculateTotal(UUID cartId) {
        Cart cart = findById(cartId);
        cart.calculateTotal();
        Cart saved = cartRepository.save(cart);
        cartEventPort.sendCartUpdate(saved);
        return saved;
    }
}
