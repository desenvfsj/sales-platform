package br.com.fsj.salesplatform.application.ports.in;

import br.com.fsj.salesplatform.application.core.domain.Product;

public interface FindProductByIdInputPort {
    Product findById(String id);
}
