package br.com.jacto.trevo.service.client;

import br.com.jacto.trevo.controller.product.dto.ProductDto;
import br.com.jacto.trevo.controller.product.form.ProductForm;
import br.com.jacto.trevo.controller.product.form.ProductUpdateForm;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.ClientRepository;
import br.com.jacto.trevo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ClientRepository clientRepository;

    public List<ProductDto> getAll() {
        return productRepository.findAll().stream().map(ProductDto::new).toList();
    }

    public Product create(ProductForm product) {
        if (product.getCreateAt() == null) {
            product.setCreateAt(LocalDate.now());
        }
        return productRepository.save(new Product(product.getProductName(), product.getStatus(),
                product.getDescription(), product.getAreaSize(), product.getCreateAt()));
    }

    public Optional<Product> getId(UUID id) {
        return productRepository.findById(id);
    }

    public void delete(UUID id) {
        productRepository.deleteById(id);
    }

    public Optional<Product> update(ProductUpdateForm product) {

        Optional<Product> findProduct = getId(product.getProductId());

        if (findProduct.isEmpty()) return findProduct;

        if (product.getProductName() != null && !product.getProductName().trim().isEmpty()) {
            findProduct.get().setProductName(product.getProductName());
        }

        if (product.getAreaSize() != null) {
            findProduct.get().setAreaSize(product.getAreaSize());
        }

        if (product.getDescription() != null && !product.getDescription().trim().isEmpty()) {
            findProduct.get().setDescription(product.getDescription());
        }

        if (product.getStatus() != null) {
            findProduct.get().setStatus(product.getStatus());
        }

        if (product.getCreateAt() != null) {
            findProduct.get().setCreateAt(product.getCreateAt());
        }

        return Optional.of(productRepository.save(findProduct.get()));

    }
}
