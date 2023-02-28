package br.com.jacto.trevo.controller.image;

import br.com.jacto.trevo.config.security.TokenService;
import br.com.jacto.trevo.controller.image.dto.ImageDto;
import br.com.jacto.trevo.controller.image.dto.ProductImageCreateDto;
import br.com.jacto.trevo.controller.image.dto.ProductImageDto;
import br.com.jacto.trevo.controller.image.form.ImageDeleteForm;
import br.com.jacto.trevo.controller.image.form.ProductImageForm;
import br.com.jacto.trevo.model.product.Image;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.ManagerRepository;
import br.com.jacto.trevo.service.product.ImageService;
import org.checkerframework.checker.nullness.Opt;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(ImageController.class)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
public class ImageControllerTest {

    @MockBean
    ImageService imageService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private ManagerRepository managerRepository;

    @Autowired
    private JacksonTester<ImageDto> imageDtoJson;
    @Autowired
    private JacksonTester<ProductImageDto> productImageDtoJson;
    @Autowired
    private JacksonTester<ProductImageCreateDto> productImageCreateDtoJson;
    @Autowired
    private JacksonTester<ProductImageForm> productImageFormJson;
    @Autowired
    private JacksonTester<ImageDeleteForm> imageDeleteFormJson;


    public Product product = new Product("Trator Jacto", true, "Trator jacto para agricultura", 120.0, LocalDate.ofEpochDay(2023 - 02 - 14));
    byte[] testBytes = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05};
    public Image image = new Image(testBytes, product);

    @Test
    @DisplayName("Mostre a imagem conforme o id dado")
    public void getImgId() throws Exception {
        UUID imgID = UUID.randomUUID();
        image.setImageId(imgID);
        byte[] imageBytes = "test image".getBytes();
        ByteArrayResource imgDto = new ByteArrayResource(imageBytes);

        when(imageService.getImage(imgID)).thenReturn(Optional.of(imgDto));

        var response = mockMvc.perform(
                        get("/images/" + imgID)
                                .contentType(MediaType.APPLICATION_JSON)

                ).andReturn()
                .getResponse();

        String resultBytes = response.getContentAsString();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertEquals("test image", resultBytes);
    }

    @Test
    @DisplayName("Retorne not found ao nao encontrar imagem cadastrada")
    public void getImgIdCase2() throws Exception {
        UUID imgID = UUID.randomUUID();
        image.setImageId(imgID);

        when(imageService.getImage(imgID)).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                        get("/images/" + imgID)
                                .contentType(MediaType.APPLICATION_JSON)

                ).andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

    }

    @Test
    @DisplayName("Retorne bad request ao informar o id em formato errado")
    public void getImgIdCase3() throws Exception {

        var response = mockMvc.perform(
                        get("/images/" + 1)
                                .contentType(MediaType.APPLICATION_JSON)

                ).andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

    }

    @Test
    @DisplayName("Mostre a imagem com seu respectivo produto conforme o id do produto")
    public void getProductImageId() throws Exception {
        UUID productId = UUID.randomUUID();
        List<Image> listImg = new ArrayList<Image>();
        listImg.add(image);
        product.setImgs(listImg);
        ProductImageDto productImg = new ProductImageDto(product);

        when(imageService.getImageProduct(productId)).thenReturn(Optional.of(productImg));

        var response = mockMvc.perform(
                        get("/products/images/" + productId)
                                .contentType(MediaType.APPLICATION_JSON)

                ).andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = productImageDtoJson.write(productImg).getJson();

        assertEquals(jsonExpect, response.getContentAsString());

    }

    @Test
    @DisplayName("caso nao encontre o produto da imagem cadastrado retorne not found")
    public void getProductImageIdCase2() throws Exception {
        UUID productId = UUID.randomUUID();
        image.setImageId(productId);
        List<Image> listImg = new ArrayList<Image>();
        listImg.add(image);
        product.setImgs(listImg);
        ProductImageDto productImg = new ProductImageDto(product);

        when(imageService.getImageProduct(productId)).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                        get("/products/images/" + productId)
                                .contentType(MediaType.APPLICATION_JSON)

                ).andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

    }

    @Test
    @DisplayName("id informado em formato incorreto deve retornar bad request")
    public void getProductImageIdCase3() throws Exception {
        var response = mockMvc.perform(
                        get("/products/images/" + 1)
                                .contentType(MediaType.APPLICATION_JSON)

                ).andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @DisplayName("Registrar uma imagem")
    public void uploadImg() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID imgId = UUID.randomUUID();
        image.setImageId(imgId);
        ProductImageCreateDto productCreateImg = new ProductImageCreateDto(image);

        Path tempFile = Files.createTempFile("test", ".jpg");
        Files.write(tempFile, "Teste".getBytes());
        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", Files.newInputStream(tempFile));


        when(imageService.upload(any())).thenReturn(Optional.of(productCreateImg));

        var response = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/products/images")
                                .file(imageFile)
                                .param("productId", productId.toString())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andReturn()
                .getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertTrue(response.containsHeader("Location"));
        var jsonExpect = productImageCreateDtoJson.write(productCreateImg).getJson();

        assertEquals(jsonExpect, response.getContentAsString());


    }

    @Test
    @DisplayName("Ao fazer upload e o produto nao for cadastrado retorne not found ")
    public void uploadImgCase2() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID imgId = UUID.randomUUID();
        image.setImageId(imgId);

        Path tempFile = Files.createTempFile("test", ".jpg");
        Files.write(tempFile, "Teste".getBytes());
        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", Files.newInputStream(tempFile));


        when(imageService.upload(any())).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/products/images")
                                .file(imageFile)
                                .param("productId", productId.toString())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

    }

    @Test
    @DisplayName("Caso o campos obrigatorio nao for preenchido retorne bad request")
    public void uploadImgCase3() throws Exception {

        var response = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/products/images")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

    }

    @Test
    @DisplayName("Nao permitido fazer upload de imagem")
    public void uploadImgCase4() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID imgId = UUID.randomUUID();
        image.setImageId(imgId);

        Path tempFile = Files.createTempFile("test", ".jpg");
        Files.write(tempFile, "Teste".getBytes());
        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", Files.newInputStream(tempFile));


        when(imageService.upload(any())).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

        var response = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/products/images")
                                .file(imageFile)
                                .param("productId", productId.toString())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                ).andReturn()
                .getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());

    }

    @Test
    @DisplayName("Atualizacao de uma imagem")
    public void updateImg() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID imgId = UUID.randomUUID();
        image.setImageId(imgId);

        Path tempFile = Files.createTempFile("test", ".jpg");
        Files.write(tempFile, "Teste".getBytes());
        MockMultipartFile imageFile = new MockMultipartFile("img", "test.jpg", "image/jpeg", Files.newInputStream(tempFile));

        ImageDto imgDto = new ImageDto(image);

        when(imageService.updateImage(any())).thenReturn(Optional.of(imgDto));

        var response = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/images")
                                .file(imageFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(request -> {
                                    request.setMethod("PUT");
                                    request.setParameter("productId", productId.toString());
                                    request.setParameter("imageId", imgId.toString());
                                    return request;
                                })
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = imageDtoJson.write(imgDto).getJson();

        assertEquals(jsonExpect, response.getContentAsString());

    }

    @Test
    @DisplayName("Imagem ou produto nao cadastrado retorna not found")
    public void updateImgCase2() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID imgId = UUID.randomUUID();
        image.setImageId(imgId);

        Path tempFile = Files.createTempFile("test", ".jpg");
        Files.write(tempFile, "Teste".getBytes());
        MockMultipartFile imageFile = new MockMultipartFile("img", "test.jpg", "image/jpeg", Files.newInputStream(tempFile));

        ImageDto imgDto = new ImageDto(image);

        when(imageService.updateImage(any())).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/images")
                                .file(imageFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(request -> {
                                    request.setMethod("PUT");
                                    request.setParameter("productId", productId.toString());
                                    request.setParameter("imageId", imgId.toString());
                                    return request;
                                })
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

    }

    @Test
    @DisplayName("Campos obrigatorio nao informado ao atualizar deve retorna bad request")
    public void updateImgCase3() throws Exception {


        var response = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/images")
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @DisplayName("Nao permissao para fazer atualizacao na imagem")
    public void updateImgCase4() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID imgId = UUID.randomUUID();
        image.setImageId(imgId);

        Path tempFile = Files.createTempFile("test", ".jpg");
        Files.write(tempFile, "Teste".getBytes());
        MockMultipartFile imageFile = new MockMultipartFile("img", "test.jpg", "image/jpeg", Files.newInputStream(tempFile));

        ImageDto imgDto = new ImageDto(image);

        when(imageService.updateImage(any())).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

        var response = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/images")
                                .file(imageFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(request -> {
                                    request.setMethod("PUT");
                                    request.setParameter("productId", productId.toString());
                                    request.setParameter("imageId", imgId.toString());
                                    return request;
                                })
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());

    }


    @Test
    @DisplayName("Deletar uma imagem")
    public void deleteImg() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID imgId = UUID.randomUUID();
        image.setImageId(imgId);

        ImageDeleteForm form = new ImageDeleteForm();
        form.setImageId(imgId);
        form.setProductId(productId);

        ImageDto imgDto = new ImageDto(image);

        when(imageService.deleteImage(any())).thenReturn(true);

        var response = mockMvc.perform(
                        delete("/images")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(imageDeleteFormJson.write(form).getJson())

                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    @DisplayName("Ao deleta e nao encontrar produto, imagem ou imagem que nao corresponde o produto deve retornar not found")
    public void deleteImgCase2() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID imgId = UUID.randomUUID();
        image.setImageId(imgId);

        ImageDeleteForm form = new ImageDeleteForm();
        form.setImageId(imgId);
        form.setProductId(productId);

        when(imageService.deleteImage(any())).thenReturn(false);

        var response = mockMvc.perform(
                        delete("/images")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(imageDeleteFormJson.write(form).getJson())

                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    @DisplayName("Campos obrigatorio no formato incorreto ou nao informado deve retornar bad request")
    public void deleteImgCase3() throws Exception {
        ImageDeleteForm form = new ImageDeleteForm();
        var response = mockMvc.perform(
                        delete("/images")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(imageDeleteFormJson.write(form).getJson())

                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @DisplayName("nao permissao para deletar imagem")
    public void deleteImgCase4() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID imgId = UUID.randomUUID();
        image.setImageId(imgId);

        ImageDeleteForm form = new ImageDeleteForm();
        form.setImageId(imgId);
        form.setProductId(productId);

        when(imageService.deleteImage(any())).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

        var response = mockMvc.perform(
                        delete("/images")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(imageDeleteFormJson.write(form).getJson())

                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

}
