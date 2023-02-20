package br.com.jacto.trevo.controller.product;

import br.com.jacto.trevo.controller.product.dto.ProductCreateDto;
import br.com.jacto.trevo.controller.product.dto.ProductDetailDto;
import br.com.jacto.trevo.controller.product.dto.ProductDto;
import br.com.jacto.trevo.controller.product.dto.ProductOrderDto;
import br.com.jacto.trevo.controller.product.form.ProductCultureDeleteForm;
import br.com.jacto.trevo.controller.product.form.ProductCultureForm;
import br.com.jacto.trevo.controller.product.form.ProductForm;
import br.com.jacto.trevo.controller.product.form.ProductUpdateForm;
import br.com.jacto.trevo.model.product.Culture;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.service.product.CultureService;
import br.com.jacto.trevo.service.product.ProductService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CultureService cultureService;

    @GetMapping
    public Page<ProductDto> getProduct(Pageable pagination) {
        return productService.getAll(pagination);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDto> getProductId(@PathVariable UUID id) {
        Optional<ProductDetailDto> product = productService.getId(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ProductOrderDto> getProductOrder(@PathVariable UUID id) {
        Optional<ProductOrderDto> productOrder = productService.productOrder(id);
        return productOrder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    @Transactional
    public ResponseEntity<ProductCreateDto> createProduct(@RequestBody @Valid ProductForm product, UriComponentsBuilder uriBuilder) {
        ProductCreateDto save = productService.create(product);
        URI uri = uriBuilder.path("/products/{id}").buildAndExpand(save.getProductId()).toUri();
        return ResponseEntity.created(uri).body(save);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Product> updateProduct(@RequestBody @Valid ProductUpdateForm product) {
        Optional<Product> updateProduct = productService.update(product);
        return updateProduct.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/cultures")
    @Transactional
    public ResponseEntity<Culture> updateCulture(@RequestBody @Valid ProductCultureForm culture) {
        Optional<Culture> updateCulture = cultureService.update(culture);
        return updateCulture.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/cultures")
    @Transactional
    public ResponseEntity<Culture> deleteCulture(@RequestBody @Valid ProductCultureDeleteForm culture) {
        Boolean findCulture = cultureService.delete(culture);
        return findCulture ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        Boolean deleteProduct = productService.delete(id);
        return deleteProduct ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


}
