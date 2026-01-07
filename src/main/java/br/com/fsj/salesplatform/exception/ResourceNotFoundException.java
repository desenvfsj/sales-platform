package br.com.fsj.salesplatform.exception;

/**
 * Exceção lançada quando um recurso solicitado não é encontrado.
 * 
 * <p>Esta exceção resulta em uma resposta HTTP 404 (Not Found) seguindo
 * o padrão RFC 9457 (Problem Details).</p>
 * 
 * <p>Use esta exceção para qualquer tipo de recurso não encontrado:
 * carrinho, produto, item, cliente, etc.</p>
 * 
 * <p><strong>Exemplos de uso:</strong></p>
 * <pre>{@code
 * // Carrinho não encontrado
 * public Cart findById(Long id) {
 *     return repository.findById(id)
 *         .orElseThrow(() -> new ResourceNotFoundException(
 *             "Carrinho não encontrado com id: " + id
 *         ));
 * }
 * 
 * // Produto não encontrado
 * public Product findProduct(String productId) {
 *     return productRepository.findById(productId)
 *         .orElseThrow(() -> new ResourceNotFoundException(
 *             "Produto não encontrado com id: " + productId
 *         ));
 * }
 * 
 * // Item do carrinho não encontrado
 * public CartItem findItem(Long itemId) {
 *     return itemRepository.findById(itemId)
 *         .orElseThrow(() -> new ResourceNotFoundException(
 *             "Item do carrinho não encontrado com id: " + itemId
 *         ));
 * }
 * }</pre>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Construtor com mensagem de erro.
     * 
     * @param message mensagem descritiva do erro (ex: "Carrinho não encontrado com id: 123")
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Construtor com mensagem e causa.
     * 
     * @param message mensagem descritiva do erro
     * @param cause causa raiz da exceção
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

