package br.com.jacto.trevo.controller.image;

import br.com.jacto.trevo.controller.image.dto.ImageDto;
import br.com.jacto.trevo.controller.image.dto.ProductImageCreateDto;
import br.com.jacto.trevo.controller.image.dto.ProductImageDto;
import br.com.jacto.trevo.controller.image.form.ImageDeleteForm;
import br.com.jacto.trevo.controller.image.form.ImageUpdateForm;
import br.com.jacto.trevo.controller.image.form.ProductImageForm;
import br.com.jacto.trevo.service.product.ImageService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping
public class ImageController {

    @Autowired
    ImageService imageService;

    @GetMapping("/images/{id}")
    public ResponseEntity<ImageDto> getId(@PathVariable UUID id) {
        Optional<ImageDto> findImage = imageService.getImage(id);
        return findImage.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/products/images/{id}")
    public ResponseEntity<ProductImageDto> getImageProductId(@PathVariable UUID id) {
        Optional<ProductImageDto> findImage = imageService.getImageProduct(id);
        return findImage.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/products/images")
    @Transactional
    public ResponseEntity<ProductImageCreateDto> uploadImage(@ModelAttribute @Valid ProductImageForm image, UriComponentsBuilder uriBuilder) throws IOException {
        Optional<ProductImageCreateDto> img = imageService.upload(image);
        if (img.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        URI uri = uriBuilder.path("/images/{id}").buildAndExpand(img.get().getImageId()).toUri();
        return ResponseEntity.created(uri).body(img.get());
    }

    @PutMapping("/images")
    @Transactional
    public ResponseEntity<ImageDto> updateImage(@ModelAttribute @Valid ImageUpdateForm img) throws IOException {
        Optional<ImageDto> updateImage = imageService.updateImage(img);
        return updateImage.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/images")
    @Transactional
    public ResponseEntity<Void> deleteImage(@RequestBody @Valid ImageDeleteForm img) {
        Boolean deleteImage = imageService.deleteImage(img);
        return deleteImage ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
