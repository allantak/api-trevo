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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/products")
@Tag(name = "Produto", description = "Gerenciamento de produtos")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CultureService cultureService;

    @GetMapping
    @Operation(summary = "Lista todos os produtos", description = "Por padrão a quantidade de produto em uma pagina é 20, mas pode ser mudado")
    public Page<ProductDto> getProduct(@ParameterObject Pageable pagination) {
        return productService.getAll(pagination);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Mostra o produto com mais detalhes")
    public ResponseEntity<ProductDetailDto> getProductId(@PathVariable UUID id) {
        Optional<ProductDetailDto> product = productService.getId(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/orders/{id}")
    @Operation(summary = "Mostra o produto com seus detalhas e suas culturas")
    public ResponseEntity<ProductOrderDto> getProductOrder(@PathVariable UUID id) {
        Optional<ProductOrderDto> productOrder = productService.productOrder(id);
        return productOrder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Registro de produto", description = "Não colocar o creatAt pegará a data atual por padrão")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<ProductCreateDto> createProduct(@RequestBody @Valid ProductForm product, UriComponentsBuilder uriBuilder) {
        ProductCreateDto save = productService.create(product);
        URI uri = uriBuilder.path("/products/{id}").buildAndExpand(save.getProductId()).toUri();
        return ResponseEntity.created(uri).body(save);
    }

    @PutMapping
    @Transactional
    @Operation(summary = "Atualização do produto")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<Product> updateProduct(@RequestBody @Valid ProductUpdateForm product) {
        Optional<Product> updateProduct = productService.update(product);
        return updateProduct.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/cultures")
    @Transactional
    @Operation(summary = "Atualização da cultura do produto")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<Culture> updateCulture(@RequestBody @Valid ProductCultureForm culture) {
        Optional<Culture> updateCulture = cultureService.update(culture);
        return updateCulture.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Excluir produto", description = "Culturas e pedidos vinculada ao produto serão excluído")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        Boolean deleteProduct = productService.delete(id);
        return deleteProduct ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/cultures")
    @Transactional
    @Operation(summary = "Excluir cultura do produto")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<Culture> deleteCulture(@RequestBody @Valid ProductCultureDeleteForm culture) {
        Boolean findCulture = cultureService.delete(culture);
        return findCulture ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


}
