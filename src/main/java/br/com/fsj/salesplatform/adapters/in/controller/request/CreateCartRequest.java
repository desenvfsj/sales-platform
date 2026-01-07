package br.com.fsj.salesplatform.adapters.in.controller.request;

import br.com.fsj.salesplatform.application.core.domain.CartChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCartRequest(
        @NotBlank(message = "ID do carrinho é obrigatório")
        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", 
                 message = "ID deve ser um UUID válido")
        String id,
        
        @NotBlank(message = "CPF do cliente é obrigatório")
        @Size(min = 11, max = 11, message = "CPF deve conter exatamente 11 dígitos")
        @Pattern(regexp = "^[0-9]{11}$", message = "CPF deve conter apenas números")
        String customerCpf,
        
        @NotNull(message = "Canal é obrigatório")
        CartChannel channel
) {
}
