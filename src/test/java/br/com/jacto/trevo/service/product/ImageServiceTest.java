package br.com.jacto.trevo.service.product;
import static org.junit.Assert.*;

import br.com.jacto.trevo.controller.image.dto.ImageDto;
import br.com.jacto.trevo.controller.image.dto.ProductImageCreateDto;
import br.com.jacto.trevo.controller.image.form.ImageDeleteForm;
import br.com.jacto.trevo.controller.image.form.ImageUpdateForm;
import br.com.jacto.trevo.controller.image.form.ProductImageForm;
import br.com.jacto.trevo.model.product.Image;
import br.com.jacto.trevo.model.product.Product;
import jakarta.transaction.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
public class ImageServiceTest {

    @Autowired
    ImageService imageService;

    @Autowired
    TestEntityManager em;

    public Product product = new Product("Trator Jacto", true, "Trator jacto para agricultura", 120.0, LocalDate.ofEpochDay(2023 - 02 - 14));


    @Test
    public void aoFazerUploadComIdDoProdutoExistenteRetorneUpload() throws IOException {
        em.persist(product);
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "Hello, World!".getBytes());

        ProductImageForm form = new ProductImageForm();
        form.setImage(file);
        form.setProductId(product.getProductId());

        Optional<ProductImageCreateDto> upload = imageService.upload(form);

        assertNotNull(upload);
        assertNotNull(upload.get().getImageId());
    }

    @Test
    public void aoFazerUploadComIdDoProdutoNaoExistenteRetorneNull() throws IOException {
        em.persist(product);
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "Hello, World!".getBytes());

        ProductImageForm form = new ProductImageForm();
        form.setImage(file);
        form.setProductId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));

        Optional<ProductImageCreateDto> upload = imageService.upload(form);

        assertEquals(Optional.empty(), upload);
    }


    @Test
    public void updateDaImageComIdDoProdutoEIdDaImageCorretamenteRetorneImageAtualizada() throws IOException {
        em.persist(product);
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "Hello, World!".getBytes());

        Image image = new Image(file.getBytes(), product);
        em.persist(image);

        ImageUpdateForm form = new ImageUpdateForm();
        form.setImageId(image.getImageId());
        form.setProductId(product.getProductId());
        form.setImg(file);

        Optional<ImageDto> upload = imageService.updateImage(form);

        assertNotNull(upload);
        assertEquals(image.getImageId(), upload.get().getImageId());
        assertEquals(file.getBytes(), upload.get().getImg());
    }


    @Test
    public void updateDaImageIdDaImageCorretaEIdProductNaoCorrespondenteRetornaOptinalEmpty() throws IOException {
        em.persist(product);
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "Hello, World!".getBytes());

        Image image = new Image(file.getBytes(), product);
        em.persist(image);

        ImageUpdateForm form = new ImageUpdateForm();
        form.setImageId(image.getImageId());
        form.setProductId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        form.setImg(file);

        Optional<ImageDto> upload = imageService.updateImage(form);

        assertEquals(Optional.empty(), upload);
    }

    @Test
    public void updateDaImageIdNaoCorrespondenteEIdProdutoExistenteRetornaOptinalEmpty() throws IOException {
        em.persist(product);
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "Hello, World!".getBytes());

        Image image = new Image(file.getBytes(), product);
        em.persist(image);

        ImageUpdateForm form = new ImageUpdateForm();
        form.setImageId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        form.setProductId(product.getProductId());
        form.setImg(file);

        Optional<ImageDto> upload = imageService.updateImage(form);

        assertEquals(Optional.empty(), upload);
    }


    @Test
    public void deleteDaImageComIdDoProdutoEIdDaImageCorretamente() throws IOException {
        em.persist(product);
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "Hello, World!".getBytes());

        Image image = new Image(file.getBytes(), product);
        em.persist(image);

        ImageDeleteForm form = new ImageDeleteForm();
        form.setImageId(image.getImageId());
        form.setProductId(product.getProductId());

        Boolean upload = imageService.deleteImage(form);

        assertNotNull(upload);
        assertTrue(upload);
    }


    @Test
    public void deleteDaImageIdDaImageCorretaEIdProductNaoCorrespondenteRetornaOptinalEmpty() throws IOException {
        em.persist(product);
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "Hello, World!".getBytes());

        Image image = new Image(file.getBytes(), product);
        em.persist(image);

        ImageDeleteForm form = new ImageDeleteForm();
        form.setImageId(image.getImageId());
        form.setProductId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));

        Boolean upload = imageService.deleteImage(form);

        assertFalse(upload);
    }

    @Test
    public void deleteDaImageIdNaoCorrespondenteEIdProdutoExistenteRetornaOptinalEmpty() throws IOException {
        em.persist(product);
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "Hello, World!".getBytes());

        Image image = new Image(file.getBytes(), product);
        em.persist(image);

        ImageDeleteForm form = new ImageDeleteForm();
        form.setImageId(UUID.fromString("a6d8726e-d3d3-410e-86be-3404c68959cb"));
        form.setProductId(product.getProductId());

        Boolean upload = imageService.deleteImage(form);

        assertFalse(upload);
    }




}
