package org.example.digijed.services;

import lombok.RequiredArgsConstructor;
import org.example.digijed.models.Product;
import org.example.digijed.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        log.debug("Retrieving all products");
        return productRepository.findAll();
    }

    public Product getProductById(long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product addProduct(Product product) {
        log.info("Saving product: {}", product.getName());
        return productRepository.save(product);
    }
}
