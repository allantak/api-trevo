package br.com.jacto.trevo.service.product;

import br.com.jacto.trevo.controller.product.dto.ProductCreateDto;
import br.com.jacto.trevo.controller.product.dto.ProductDetailDto;
import br.com.jacto.trevo.controller.product.dto.ProductDto;
import br.com.jacto.trevo.controller.product.dto.ProductOrderDto;
import br.com.jacto.trevo.controller.product.form.ProductForm;
import br.com.jacto.trevo.controller.product.form.ProductUpdateForm;
import br.com.jacto.trevo.model.product.Culture;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.CultureRepository;
import br.com.jacto.trevo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CultureRepository cultureRepository;

    public Page<ProductDto> getAll(Pageable pagination) {
        return productRepository.findAll(pagination).map(ProductDto::new);
    }

    public ProductCreateDto create(ProductForm product) {
        if (product.getCreateAt() == null) {
            product.setCreateAt(LocalDate.now());
        }
        Product createProduct = productRepository.save(new Product(product.getProductName(), product.getStatus(),
                product.getDescription(), product.getAreaSize(), product.getCreateAt()));

        for (String culture : product.getCultures()) {
            Culture listCulture = new Culture(culture, createProduct);
            cultureRepository.save(listCulture);
        }

        return new ProductCreateDto(createProduct);
    }

    public Optional<ProductDetailDto> getId(UUID id) {
        return productRepository.findById(id).map(ProductDetailDto::new);
    }

    public Optional<Product> delete(UUID id) {
        Optional<Product> findProduct = productRepository.findById(id);
        if(findProduct.isEmpty()){
            return Optional.empty();
        }
        productRepository.deleteById(id);
        return findProduct;
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
