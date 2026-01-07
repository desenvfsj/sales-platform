package br.com.fsj.salesplatform.exception;

/**
 * Exceção para erros de regra de negócio.
 * 
 * <p>Esta exceção é usada para representar violações de regras de negócio,
 * estados inválidos ou conflitos de operação. Pode resultar em diferentes
 * status HTTP dependendo do contexto:</p>
 * <ul>
 *   <li>400 (Bad Request) - violação de regra de negócio</li>
 *   <li>409 (Conflict) - conflito de estado</li>
 *   <li>422 (Unprocessable Entity) - operação semanticamente inválida</li>
 * </ul>
 * 
 * <p><strong>Exemplos de uso:</strong></p>
 * <pre>{@code
 * // Estado inválido
 * if (!cart.isOpen()) {
 *     throw new BusinessException(
 *         "Não é possível modificar carrinho com status: " + cart.getStatus()
 *     );
 * }
 * 
 * // Carrinho vazio
 * if (cart.getItems().isEmpty()) {
 *     throw new BusinessException("Não é possível finalizar carrinho vazio");
 * }
 * 
 * // Quantidade inválida
 * if (quantity <= 0) {
 *     throw new BusinessException("Quantidade deve ser maior que zero");
 * }
 * }</pre>
 * 
 * @author Sales Platform Team
 * @since 1.0.0
 */
public class BusinessException extends RuntimeException {

    /**
     * Construtor com mensagem de erro.
     * 
     * @param message mensagem descritiva do erro de negócio
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa.
     * 
     * @param message mensagem descritiva do erro de negócio
     * @param cause causa raiz da exceção
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

