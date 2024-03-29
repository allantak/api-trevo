package br.com.jacto.trevo.service.product;

import br.com.jacto.trevo.controller.image.dto.ProductImageCreateDto;
import br.com.jacto.trevo.controller.image.dto.ProductImageDto;
import br.com.jacto.trevo.controller.image.form.ImageDeleteForm;
import br.com.jacto.trevo.controller.image.form.ImageUpdateForm;
import br.com.jacto.trevo.controller.image.form.ProductImageForm;
import br.com.jacto.trevo.model.account.Account;
import br.com.jacto.trevo.model.product.Image;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.ImageRepository;
import br.com.jacto.trevo.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ImageServiceTest {

    @InjectMocks
    ImageService imageService;

    @Mock
    private ImageRepository imageRepository;
    @Mock
    private ProductRepository productRepository;

    public Account account = new Account("test", "12345", "test", Account.Role.COLABORADOR);
    public Product product = new Product("Trator Jacto", Product.Status.DISPONIVEL, Product.Category.ELETRICO, "Trator jacto para agricultura", 120.0, 2.0, LocalDateTime.of(2023, 3, 28, 10, 30, 15, 500000000), account);

    byte[] testBytes = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05};
    public Image image = new Image(testBytes, product);

    @Test
    @DisplayName("mostra a imagem pelo id")
    public void getImage() throws IOException {
        image.setImageId(UUID.randomUUID());
        when(imageRepository.findById(any())).thenReturn(Optional.ofNullable(image));

        Optional<ByteArrayResource> response = imageService.getImage(image.getImageId());

        assertNotNull(response);
        assertEquals(image.getImg(), response.get().getByteArray());
    }

    @Test
    @DisplayName("id da imagem nao encontrado")
    public void getImageCase2() throws IOException {
        image.setImageId(UUID.randomUUID());

        when(imageRepository.findById(any())).thenReturn(Optional.empty());

        Optional<ByteArrayResource> response = imageService.getImage(image.getImageId());

        assertEquals(Optional.empty(), response);
    }

    @Test
    @DisplayName("mostra a imagem relacionado com o id do produto")
    public void getImageProduct() throws IOException {
        image.setImageId(UUID.randomUUID());
        List<Image> listImg = new ArrayList<Image>();
        listImg.add(image);
        product.setImgs(listImg);
        product.setProductId(UUID.randomUUID());

        when(productRepository.findById(any())).thenReturn(Optional.ofNullable(product));

        Optional<ProductImageDto> response = imageService.getImageProduct(image.getImageId());

        assertNotNull(response);
        assertNotNull(response.get().getProductId());
    }

    @Test
    @DisplayName("Caso nao ache produto registrado de imagem")
    public void getImageProductCase2() throws IOException {
        image.setImageId(UUID.randomUUID());

        when(productRepository.findById(any())).thenReturn(Optional.empty());

        Optional<ProductImageDto> response = imageService.getImageProduct(image.getImageId());

        assertEquals(Optional.empty(), response);
    }

    @Test
    @DisplayName("Cadastro de image")
    public void uploadImage() throws IOException {
        Path tempFile = Files.createTempFile("test", ".jpg");
        Files.write(tempFile, "Teste".getBytes());
        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", Files.newInputStream(tempFile));

        UUID productId = UUID.randomUUID();
        product.setProductId(productId);

        List<Image> listImg = new ArrayList<Image>();
        listImg.add(image);
        product.setImgs(listImg);

        image.setImageId(UUID.randomUUID());

        ProductImageForm form = new ProductImageForm();
        form.setImage(imageFile);
        form.setProductId(productId);


        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(imageRepository.save(any())).thenReturn(image);

        Optional<ProductImageCreateDto> upload = imageService.upload(form);

        assertNotNull(upload);
    }

    @Test
    @DisplayName("cadastro com id incorreto de produto relacionado com a imagem")
    public void uploadImageCase2() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "Hello, World!".getBytes());

        ProductImageForm form = new ProductImageForm();
        form.setImage(file);
        form.setProductId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));

        when(productRepository.findById(any())).thenReturn(Optional.empty());
        when(imageRepository.save(any())).thenReturn(image);

        Optional<ProductImageCreateDto> upload = imageService.upload(form);

        assertEquals(Optional.empty(), upload);
    }


    @Test
    @DisplayName("atualizacao de imagem")
    public void updateImage() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "Hello, World!".getBytes());
        product.setProductId(UUID.randomUUID());
        Image image = new Image(file.getBytes(), product);

        ImageUpdateForm form = new ImageUpdateForm();
        form.setImageId(image.getImageId());
        form.setProductId(product.getProductId());
        form.setImg(file);

        when(imageRepository.findById(any())).thenReturn(Optional.of(image));
        when(imageRepository.save(any())).thenReturn(image);

        Optional<ProductImageCreateDto> upload = imageService.updateImage(form);

        assertNotNull(upload);
        assertEquals(image.getImageId(), upload.get().getImageId());
    }


    @Test
    @DisplayName("atualizacao com id do produto incorreto")
    public void updateImageCase2() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "Hello, World!".getBytes());
        product.setProductId(UUID.randomUUID());
        Image image = new Image(file.getBytes(), product);

        ImageUpdateForm form = new ImageUpdateForm();
        form.setImageId(image.getImageId());
        form.setProductId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        form.setImg(file);


        when(imageRepository.findById(any())).thenReturn(Optional.of(image));
        when(imageRepository.save(any())).thenReturn(image);

        Optional<ProductImageCreateDto> upload = imageService.updateImage(form);

        assertEquals(Optional.empty(), upload);
    }

    @Test
    @DisplayName("Ao atualizar e nao encontrar id da imagem")
    public void updateImageCase3() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "Hello, World!".getBytes());
        product.setProductId(UUID.randomUUID());
        Image image = new Image(file.getBytes(), product);

        ImageUpdateForm form = new ImageUpdateForm();
        form.setImageId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        form.setProductId(product.getProductId());
        form.setImg(file);


        when(imageRepository.findById(any())).thenReturn(Optional.empty());
        when(imageRepository.save(any())).thenReturn(image);

        Optional<ProductImageCreateDto> upload = imageService.updateImage(form);

        assertEquals(Optional.empty(), upload);
    }


    @Test
    @DisplayName("delete de imagem")
    public void deleteImage() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "Hello, World!".getBytes());
        product.setProductId(UUID.randomUUID());
        Image image = new Image(file.getBytes(), product);


        ImageDeleteForm form = new ImageDeleteForm();
        form.setImageId(image.getImageId());
        form.setProductId(product.getProductId());

        when(imageRepository.findById(any())).thenReturn(Optional.of(image));
        Boolean upload = imageService.deleteImage(form);

        assertNotNull(upload);
        assertTrue(upload);
    }


    @Test
    @DisplayName("Ao deletar nao encontrar produto relacionado a imagem")
    public void deleteImageCase2() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "Hello, World!".getBytes());
        product.setProductId(UUID.randomUUID());
        Image image = new Image(file.getBytes(), product);

        ImageDeleteForm form = new ImageDeleteForm();
        form.setImageId(image.getImageId());
        form.setProductId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));

        when(imageRepository.findById(any())).thenReturn(Optional.of(image));
        Boolean upload = imageService.deleteImage(form);

        assertFalse(upload);
    }

    @Test
    @DisplayName("Ao deletar nao encontrar id da imagem")
    public void deleteImageCase3() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "Hello, World!".getBytes());
        product.setProductId(UUID.randomUUID());
        Image image = new Image(file.getBytes(), product);
        image.setProduct(product);

        ImageDeleteForm form = new ImageDeleteForm();
        form.setImageId(UUID.randomUUID());
        form.setProductId(UUID.randomUUID());
        when(imageRepository.findById(any())).thenReturn(Optional.of(image));
        Boolean upload = imageService.deleteImage(form);

        assertFalse(upload);
    }

}
