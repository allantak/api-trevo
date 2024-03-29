package br.com.jacto.trevo.controller.image;

import br.com.jacto.trevo.config.exception.ErrorHandler;
import br.com.jacto.trevo.config.security.SecurityFilter;
import br.com.jacto.trevo.config.security.TokenService;
import br.com.jacto.trevo.controller.image.dto.ImageDto;
import br.com.jacto.trevo.controller.image.dto.ProductImageCreateDto;
import br.com.jacto.trevo.controller.image.dto.ProductImageDto;
import br.com.jacto.trevo.controller.image.form.ImageDeleteForm;
import br.com.jacto.trevo.controller.image.form.ProductImageForm;
import br.com.jacto.trevo.model.account.Account;
import br.com.jacto.trevo.model.product.Image;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.AccountRepository;
import br.com.jacto.trevo.service.product.ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ImageControllerTest {

    @Mock
    ImageService imageService;

    private MockMvc mockMvc;

    @InjectMocks
    private ImageController imageController;

    @Mock
    private TokenService tokenService;

    @Mock
    private AccountRepository managerRepository;


    private JacksonTester<ImageDto> imageDtoJson;

    private JacksonTester<ProductImageDto> productImageDtoJson;

    private JacksonTester<ProductImageCreateDto> productImageCreateDtoJson;

    private JacksonTester<ProductImageForm> productImageFormJson;

    private JacksonTester<ImageDeleteForm> imageDeleteFormJson;


    public Account account = new Account("test", "12345", "test", Account.Role.COLABORADOR);
    public Product product = new Product("Trator Jacto", Product.Status.DISPONIVEL, Product.Category.ELETRICO, "Trator jacto para agricultura", 120.0, 2.0, LocalDateTime.of(2023, 3, 28, 10, 30, 15, 500000000), account);

    byte[] testBytes = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05};
    public Image image = new Image(testBytes, product);

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(imageController)
                .setControllerAdvice(new ErrorHandler())
                .addFilters(new SecurityFilter())
                .build();
    }

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

        ProductImageCreateDto imgDto = new ProductImageCreateDto(image);

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

        var jsonExpect = productImageCreateDtoJson.write(imgDto).getJson();

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
