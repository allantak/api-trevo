package br.com.jacto.trevo.service.product;

import br.com.jacto.trevo.controller.product.dto.ProductCreateDto;
import br.com.jacto.trevo.controller.product.dto.ProductDetailDto;
import br.com.jacto.trevo.controller.product.dto.ProductDto;
import br.com.jacto.trevo.controller.product.dto.ProductOrderDto;
import br.com.jacto.trevo.controller.product.form.ProductForm;
import br.com.jacto.trevo.controller.product.form.ProductUpdateForm;
import br.com.jacto.trevo.model.account.Account;
import br.com.jacto.trevo.model.product.Culture;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.AccountRepository;
import br.com.jacto.trevo.repository.CultureRepository;
import br.com.jacto.trevo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CultureRepository cultureRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Page<ProductDto> getAll(Pageable pagination) {
        return productRepository.findAll(pagination).map(ProductDto::new);
    }

    public Optional<ProductCreateDto> create(ProductForm product) {
        Optional<Account> findManager = accountRepository.findById(product.getAccountId());
        if (findManager.isEmpty()) {
            return Optional.empty();
        }

        Product productSave = new Product(product.getProductName(), product.getStatus(), product.getCategory(),
                product.getDescription(), product.getAreaSize(), product.getPrice(), LocalDateTime.now(), findManager.get());

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

    public Optional<ProductCreateDto> update(ProductUpdateForm product) {

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

        findProduct.get().setCreateAt(LocalDateTime.now());

        return Optional.of(productRepository.save(findProduct.get())).map(ProductCreateDto::new);

    }

    public Optional<ProductOrderDto> productOrder(UUID id) {
        return productRepository.findById(id).map(ProductOrderDto::new);
    }
}
