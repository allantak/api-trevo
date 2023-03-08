package br.com.jacto.trevo.controller.image;

import br.com.jacto.trevo.controller.image.dto.ImageDto;
import br.com.jacto.trevo.controller.image.dto.ProductImageCreateDto;
import br.com.jacto.trevo.controller.image.dto.ProductImageDto;
import br.com.jacto.trevo.controller.image.form.ImageDeleteForm;
import br.com.jacto.trevo.controller.image.form.ImageUpdateForm;
import br.com.jacto.trevo.controller.image.form.ProductImageForm;
import br.com.jacto.trevo.service.product.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping
@Tag(name = "Imagem", description = "Gerenciamento de imagens")
public class ImageController {

    @Autowired
    ImageService imageService;

    @GetMapping("/images/{id}")
    @Operation(summary = "Mostra a imagem no corpo da resposta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ByteArrayResource.class)))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<ByteArrayResource> getId(@PathVariable UUID id) {
        Optional<ByteArrayResource> findImage = imageService.getImage(id);
        return findImage.map(byteArrayResource -> ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(byteArrayResource)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/products/images/{id}")
    @Operation(summary = "Listagem das imagens do produto específico", description = "Para retorna da imagens no corpo da respota é pegar a lista de identificadores de imagem e fazer uma solicitação GET para cada um deles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductImageDto.class)))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<ProductImageDto> getImageProductId(@PathVariable UUID id) {
        Optional<ProductImageDto> findImage = imageService.getImageProduct(id);
        return findImage.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/products/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    @Operation(summary = "Registra uma imagem no produto")
    @SecurityRequirement(name = "bearer-key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductImageCreateDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<ProductImageCreateDto> uploadImage(@ModelAttribute @Valid ProductImageForm image, UriComponentsBuilder uriBuilder) throws IOException {
        Optional<ProductImageCreateDto> img = imageService.upload(image);
        if (img.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        URI uri = uriBuilder.path("/images/{id}").buildAndExpand(img.get().getImageId()).toUri();
        return ResponseEntity.created(uri).body(img.get());
    }

    @PutMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    @Operation(summary = "Atualiza a imagem do produto")
    @SecurityRequirement(name = "bearer-key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductImageCreateDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<ProductImageCreateDto> updateImage(@ModelAttribute @Valid ImageUpdateForm img) throws IOException {
        Optional<ProductImageCreateDto> updateImage = imageService.updateImage(img);
        return updateImage.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/images")
    @Transactional
    @Operation(summary = "Delete a imagem")
    @SecurityRequirement(name = "bearer-key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success no-content", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<Void> deleteImage(@RequestBody @Valid ImageDeleteForm img) {
        Boolean deleteImage = imageService.deleteImage(img);
        return deleteImage ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
