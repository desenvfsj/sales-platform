package br.com.fsj.salesplatform.application.ports.out;

import br.com.fsj.salesplatform.application.core.domain.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepositoryOutputPort {
    List<Product> findAll();
    Optional<Product> findById(String id);
}
