package br.com.fsj.salesplatform.application.ports.in;

import br.com.fsj.salesplatform.application.core.domain.Product;
import java.util.List;

public interface ListProductsInputPort {
    List<Product> listAll();
}
