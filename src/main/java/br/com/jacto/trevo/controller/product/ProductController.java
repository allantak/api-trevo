package br.com.jacto.trevo.controller.product;


import br.com.jacto.trevo.controller.product.dto.ProductDto;
import br.com.jacto.trevo.controller.product.dto.ProductOrderDto;
import br.com.jacto.trevo.controller.product.form.ProductForm;
import br.com.jacto.trevo.controller.product.form.ProductUpdateForm;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.service.product.ProductService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<ProductDto> getProduct() {
        return productService.getAll();
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Optional<ProductOrderDto>> getProductOrder(@PathVariable UUID id) {
        Optional<ProductOrderDto> productOrder = productService.productOrder(id);
        if (productOrder.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Product>> getProductId(@PathVariable UUID id) {
        Optional<Product> product = productService.getId(id);
        if (product.isEmpty()) {

            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ProductDto> createProduct(@RequestBody @Valid ProductForm product, UriComponentsBuilder uriBuilder) {
        Product save = productService.create(product);
        URI uri = uriBuilder.path("/clients/{id}").buildAndExpand(save.getProductId()).toUri();
        return ResponseEntity.created(uri).body(new ProductDto(save));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Optional<Product>> updateProduct(@RequestBody @Valid ProductUpdateForm product) {
        Optional<Product> updateProduct = productService.update(product);
        if (updateProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updateProduct);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Product> deleteProduct(@PathVariable UUID id) {
        Optional<Product> findProduct = productService.getId(id);
        if (findProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
