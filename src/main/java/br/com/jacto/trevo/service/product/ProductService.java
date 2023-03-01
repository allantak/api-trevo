package br.com.jacto.trevo.service.product;

import br.com.jacto.trevo.controller.product.dto.ProductCreateDto;
import br.com.jacto.trevo.controller.product.dto.ProductDetailDto;
import br.com.jacto.trevo.controller.product.dto.ProductDto;
import br.com.jacto.trevo.controller.product.dto.ProductOrderDto;
import br.com.jacto.trevo.controller.product.form.ProductForm;
import br.com.jacto.trevo.controller.product.form.ProductUpdateForm;
import br.com.jacto.trevo.model.manager.Manager;
import br.com.jacto.trevo.model.product.Culture;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.CultureRepository;
import br.com.jacto.trevo.repository.ManagerRepository;
import br.com.jacto.trevo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CultureRepository cultureRepository;

    @Autowired
    private ManagerRepository managerRepository;

    public Page<ProductDto> getAll(Pageable pagination) {
        return productRepository.findAll(pagination).map(ProductDto::new);
    }

    public Optional<ProductCreateDto> create(ProductForm product) {
        Optional<Manager> findManager = managerRepository.findById(product.getManagerId());
        if (findManager.isEmpty()) {
            return Optional.empty();
        }
        if (product.getCreateAt() == null) {
            product.setCreateAt(LocalDate.now());
        }

        Product productSave = new Product(product.getProductName(), product.getStatus(),
                product.getDescription(), product.getAreaSize(), product.getCreateAt(), findManager.get());

        Product createProduct = productRepository.save(productSave);

        for (String culture : product.getCultures()) {
            Culture listCulture = new Culture(culture, createProduct);
            cultureRepository.save(listCulture);
        }

        return Optional.of(new ProductCreateDto(createProduct));
    }

    public Optional<ProductDetailDto> getId(UUID id) {
        return productRepository.findById(id).map(ProductDetailDto::new);
    }

    public Boolean delete(UUID id) {
        Optional<Product> findProduct = productRepository.findById(id);
        if (findProduct.isEmpty()) {
            return false;
        }
        productRepository.deleteById(id);
        return true;
    }

    public Optional<Product> update(ProductUpdateForm product) {

        Optional<Product> findProduct = productRepository.findById(product.getProductId());

        if (findProduct.isEmpty()) return Optional.empty();

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

    public Optional<ProductOrderDto> productOrder(UUID id) {
        return productRepository.findById(id).map(ProductOrderDto::new);
    }
}
