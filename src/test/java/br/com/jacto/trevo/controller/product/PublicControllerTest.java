package br.com.jacto.trevo.controller.product;

import br.com.jacto.trevo.config.exception.ErrorHandler;
import br.com.jacto.trevo.config.security.SecurityFilter;
import br.com.jacto.trevo.config.security.TokenService;
import br.com.jacto.trevo.controller.product.dto.*;
import br.com.jacto.trevo.controller.product.form.ProductCultureDeleteForm;
import br.com.jacto.trevo.controller.product.form.ProductCultureForm;
import br.com.jacto.trevo.controller.product.form.ProductForm;
import br.com.jacto.trevo.controller.product.form.ProductUpdateForm;
import br.com.jacto.trevo.model.account.Account;
import br.com.jacto.trevo.model.order.OrderItem;
import br.com.jacto.trevo.model.product.Culture;
import br.com.jacto.trevo.model.product.Product;
import br.com.jacto.trevo.repository.AccountRepository;
import br.com.jacto.trevo.service.product.CultureService;
import br.com.jacto.trevo.service.product.ProductService;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PublicControllerTest {


    private MockMvc mockMvc;

    @InjectMocks
    private ProductController productController;

    @Mock
    ProductService productService;

    @Mock
    CultureService cultureService;

    @Mock
    private TokenService tokenService;

    @Mock
    private AccountRepository managerRepository;

    private JacksonTester<PageImpl<ProductDto>> productDtoJson;

    private JacksonTester<ProductDetailDto> productDetailDtoJson;

    private JacksonTester<ProductOrderDto> productOrderDtoJson;

    private JacksonTester<ProductCreateDto> productCreateDtoJson;

    private JacksonTester<ProductForm> productFormJson;

    private JacksonTester<ProductCultureForm> productCultureFormJson;

    private JacksonTester<ProductUpdateForm> productUpdateFormJson;

    private JacksonTester<Culture> cultureJson;

    private JacksonTester<Product> productJson;

    private JacksonTester<ProductCultureDeleteForm> productCultureDeleteFormJson;

    private JacksonTester<ProductCultureDto> productCultureDtoJson;


    public Account account = new Account("test", "12345", "test", Account.Role.COLABORADOR);
    public Product product = new Product("Trator Jacto", Product.Status.DISPONIVEL, Product.Category.ELETRICO, "Trator jacto para agricultura", 120.0, 2.0, LocalDateTime.of(2023, 3, 28, 10, 30, 15, 500000000), account);

    public OrderItem order = new OrderItem(3, account, product);
    public Culture culture = new Culture("Cerejeiras", product);

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new ErrorHandler())
                .addFilters(new SecurityFilter())
                .build();
    }


    @Test
    @DisplayName("Caso o producto nao for cadastrado retorn NotFound")
    public void productIdCase2() throws Exception {
        UUID productId = UUID.randomUUID();

        when(productService.getId(any())).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                        get("/products/" + productId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    @DisplayName("Caso o producto o id esteja em formato invalido retorne Bad Request")
    public void productIdCase3() throws Exception {

        var response = mockMvc.perform(
                        get("/products/" + 1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }


    @Test
    @DisplayName("Mostrar os pedidos de um produto especifico")
    public void productOrderId() throws Exception {
        UUID productId = UUID.randomUUID();
        product.setProductId(productId);

        List<Culture> listClient = new ArrayList<Culture>();
        listClient.add(culture);
        product.setCultures(listClient);

        List<OrderItem> orderItems = new ArrayList<OrderItem>();
        orderItems.add(order);
        product.setOrders(orderItems);

        ProductOrderDto productOrder = new ProductOrderDto(product);

        when(productService.productOrder(any())).thenReturn(Optional.of(productOrder));

        var response = mockMvc.perform(
                        get("/products/orders/" + product.getProductId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = productOrderDtoJson.write(productOrder).getJson();

        assertEquals(jsonExpect, response.getContentAsString());

    }

    @Test
    @DisplayName("Caso o produto nao seja cadastrado retorne not found")
    public void productOrderIdCase2() throws Exception {
        UUID productId = UUID.randomUUID();

        when(productService.productOrder(any())).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                        get("/products/orders/" + productId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

    }

    @Test
    @DisplayName("caso o id do produto esteja invalido retorne bad request")
    public void productOrderIdCase3() throws Exception {

        var response = mockMvc.perform(
                        get("/products/orders/" + 1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @DisplayName("Cadastra um produto e suas culturas envolvidas")
    public void createProduct() throws Exception {
        UUID productId = UUID.randomUUID();
        product.setProductId(productId);

        List<Culture> listClient = new ArrayList<Culture>();
        listClient.add(culture);
        product.setCultures(listClient);

        ProductCreateDto productCreate = new ProductCreateDto(product);

        ProductForm form = new ProductForm();
        form.setAccountId(UUID.randomUUID());
        form.setProductName(product.getProductName());
        form.setDescription(product.getDescription());
        form.setAreaSize(product.getAreaSize());
        form.setPrice(product.getPrice());
        form.setStatus(product.isStatus());
        form.setCategory(product.getCategory());
        form.setCultures(product.getCultures().stream().map(Culture::getCultureName).toList());

        when(productService.create(any())).thenReturn(Optional.of(productCreate));

        var response = mockMvc.perform(
                        post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertTrue(response.containsHeader("Location"));
        var jsonExpect = productCreateDtoJson.write(productCreate).getJson();

        assertEquals(jsonExpect, response.getContentAsString());

    }

    @Test
    @DisplayName("Cadastra um produto sem o gerente")
    public void createProductCase2() throws Exception {
        UUID productId = UUID.randomUUID();
        product.setProductId(productId);

        List<Culture> listClient = new ArrayList<Culture>();
        listClient.add(culture);
        product.setCultures(listClient);

        ProductCreateDto productCreate = new ProductCreateDto(product);

        ProductForm form = new ProductForm();
        form.setAccountId(UUID.randomUUID());
        form.setProductName(product.getProductName());
        form.setDescription(product.getDescription());
        form.setAreaSize(product.getAreaSize());
        form.setPrice(product.getPrice());
        form.setStatus(product.isStatus());
        form.setCategory(product.getCategory());
        form.setCultures(product.getCultures().stream().map(Culture::getCultureName).toList());

        when(productService.create(any())).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                        post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    @DisplayName("Cadastar um produto com falta de dados necessarios")
    public void createProductCase3() throws Exception {

        ProductForm form = new ProductForm();
        form.setAreaSize(product.getAreaSize());

        var response = mockMvc.perform(
                        post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @DisplayName("Cadastrum produto com nome de um produto ja existente deve retornar Conflict")
    public void createProductCase4() throws Exception {
        UUID productId = UUID.randomUUID();
        product.setProductId(productId);

        List<Culture> listClient = new ArrayList<Culture>();
        listClient.add(culture);
        product.setCultures(listClient);

        ProductForm form = new ProductForm();
        form.setAccountId(UUID.randomUUID());
        form.setProductName(product.getProductName());
        form.setDescription(product.getDescription());
        form.setAreaSize(product.getAreaSize());
        form.setPrice(product.getPrice());
        form.setStatus(product.isStatus());
        form.setCategory(product.getCategory());
        form.setCultures(product.getCultures().stream().map(Culture::getCultureName).toList());

        when(productService.create(any())).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        var response = mockMvc.perform(
                        post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }

    @Test
    @DisplayName("Nao permitido fazer cadastro de produto")
    public void createProductCase5() throws Exception {
        UUID productId = UUID.randomUUID();
        product.setProductId(productId);

        List<Culture> listClient = new ArrayList<Culture>();
        listClient.add(culture);
        product.setCultures(listClient);

        ProductForm form = new ProductForm();
        form.setAccountId(UUID.randomUUID());
        form.setProductName(product.getProductName());
        form.setDescription(product.getDescription());
        form.setAreaSize(product.getAreaSize());
        form.setPrice(product.getPrice());
        form.setStatus(product.isStatus());
        form.setCategory(product.getCategory());
        form.setCultures(product.getCultures().stream().map(Culture::getCultureName).toList());

        when(productService.create(any())).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

        var response = mockMvc.perform(
                        post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @DisplayName("Atualizacao do produto")
    public void updateProduct() throws Exception {
        UUID productId = UUID.randomUUID();
        product.setProductId(productId);

        ProductUpdateForm form = new ProductUpdateForm();
        form.setProductId(productId);
        form.setProductName(product.getProductName());
        form.setDescription(product.getDescription());
        form.setAreaSize(product.getAreaSize());
        form.setStatus(product.isStatus());

        when(productService.update(any())).thenReturn(Optional.of(new ProductCreateDto(product)));

        var response = mockMvc.perform(
                        put("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productUpdateFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = productCreateDtoJson.write(new ProductCreateDto(product)).getJson();

        assertEquals(jsonExpect, response.getContentAsString());
    }

    @Test
    @DisplayName("Caso o produto nao exista retorne Not found")
    public void updateProductCase2() throws Exception {
        UUID productId = UUID.randomUUID();

        ProductUpdateForm form = new ProductUpdateForm();
        form.setProductId(productId);
        form.setProductName(product.getProductName());
        form.setDescription(product.getDescription());
        form.setAreaSize(product.getAreaSize());
        form.setStatus(product.isStatus());

        when(productService.update(any())).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                        put("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productUpdateFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

    }

    @Test
    @DisplayName("Caso campos obrigatores nao seja inserido retorne Bad request")
    public void updateProductCase3() throws Exception {
        ProductUpdateForm form = new ProductUpdateForm();
        form.setProductName(product.getProductName());
        form.setDescription(product.getDescription());
        form.setAreaSize(product.getAreaSize());
        form.setStatus(product.isStatus());

        var response = mockMvc.perform(
                        put("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productUpdateFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

    }


    @Test
    @DisplayName("Caso os campos vier null deve retornar os valores ja existente")
    public void updateProductCase4() throws Exception {
        UUID productId = UUID.randomUUID();
        product.setProductId(productId);

        ProductUpdateForm form = new ProductUpdateForm();
        form.setProductId(productId);

        when(productService.update(any())).thenReturn(Optional.of(new ProductCreateDto(product)));

        var response = mockMvc.perform(
                        put("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productUpdateFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = productCreateDtoJson.write(new ProductCreateDto(product)).getJson();

        assertEquals(jsonExpect, response.getContentAsString());
    }

    @Test
    @DisplayName("Caso os campos sejam vazios deve retornar os valores ja existentes")
    public void updateProductCase5() throws Exception {
        UUID productId = UUID.randomUUID();
        product.setProductId(productId);


        ProductUpdateForm form = new ProductUpdateForm();
        form.setProductId(productId);
        form.setProductName("");
        form.setDescription("");
        form.setAreaSize(product.getAreaSize());
        form.setStatus(product.isStatus());

        when(productService.update(any())).thenReturn(Optional.of(new ProductCreateDto(product)));

        var response = mockMvc.perform(
                        put("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productUpdateFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = productCreateDtoJson.write(new ProductCreateDto(product)).getJson();

        assertEquals(jsonExpect, response.getContentAsString());
    }

    @Test
    @DisplayName("Nao permitido fazer atualizacao")
    public void updateProductCase6() throws Exception {
        UUID productId = UUID.randomUUID();

        ProductUpdateForm form = new ProductUpdateForm();
        form.setProductId(productId);
        form.setProductName(product.getProductName());
        form.setDescription(product.getDescription());
        form.setAreaSize(product.getAreaSize());
        form.setStatus(product.isStatus());

        when(productService.update(any())).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

        var response = mockMvc.perform(
                        put("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productUpdateFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());

    }

    @Test
    @DisplayName("Atualizacao de cultura em um produto")
    public void updateCulture() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID cultureId = UUID.randomUUID();

        ProductCultureForm form = new ProductCultureForm();
        form.setProductId(productId);
        form.setCultureId(cultureId);
        form.setCultureName("cultureNew");

        when(cultureService.update(any())).thenReturn(Optional.of(new ProductCultureDto(culture)));

        var response = mockMvc.perform(
                        put("/products/cultures")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productCultureFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = productCultureDtoJson.write(new ProductCultureDto(culture)).getJson();

        assertEquals(jsonExpect, response.getContentAsString());

    }

    @Test
    @DisplayName("Ao nao achar culture e produto nao cadastrado deve retornar not found")
    public void updateCultureCase2() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID cultureId = UUID.randomUUID();

        ProductCultureForm form = new ProductCultureForm();
        form.setProductId(productId);
        form.setCultureId(cultureId);
        form.setCultureName("cultureNew");

        when(cultureService.update(any())).thenReturn(Optional.empty());

        var response = mockMvc.perform(
                        put("/products/cultures")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productCultureFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    @DisplayName("Caso falte campo para fazer atualizacao deve retornar bad request")
    public void updateCultureCase3() throws Exception {

        ProductCultureForm form = new ProductCultureForm();

        var response = mockMvc.perform(
                        put("/products/cultures")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productCultureFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @DisplayName("Nao permitido fazer atualizicao de produto")
    public void updateCultureCase4() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID cultureId = UUID.randomUUID();

        ProductCultureForm form = new ProductCultureForm();
        form.setProductId(productId);
        form.setCultureId(cultureId);
        form.setCultureName("cultureNew");

        when(cultureService.update(any())).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

        var response = mockMvc.perform(
                        put("/products/cultures")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productCultureFormJson.write(form).getJson())
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }


    @Test
    @DisplayName("Ao deletar retorne No-content")
    public void deleteProductId() throws Exception {
        UUID productId = UUID.randomUUID();

        when(productService.delete(productId)).thenReturn(true);

        var response = mockMvc.perform(
                delete("/products/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)

        ).andReturn().getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());

    }

    @Test
    @DisplayName("Ao deletar com produto nao cadastrado retorne not found")
    public void deleteProductIdCase2() throws Exception {
        UUID productId = UUID.randomUUID();

        when(productService.delete(productId)).thenReturn(false);

        var response = mockMvc.perform(
                delete("/products/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)

        ).andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

    }

    @Test
    @DisplayName("Ao deletar e informar id em formato errado deve retornar bad request")
    public void deleteProductIdCase3() throws Exception {
        var response = mockMvc.perform(
                delete("/products/" + 1)
                        .contentType(MediaType.APPLICATION_JSON)

        ).andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

    }

    @Test
    @DisplayName("Nao permitido deletar produto")
    public void deleteProductIdCase4() throws Exception {
        UUID productId = UUID.randomUUID();

        when(productService.delete(productId)).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));

        var response = mockMvc.perform(
                delete("/products/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)

        ).andReturn().getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());

    }

    @Test
    @DisplayName("deletar de cultura")
    public void deleteCultureId() throws Exception {
        UUID cultureId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(cultureService.delete(any())).thenReturn(true);
        ProductCultureDeleteForm form = new ProductCultureDeleteForm();
        form.setCultureId(cultureId);
        form.setProductId(productId);


        var response = mockMvc.perform(
                delete("/products/cultures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productCultureDeleteFormJson.write(form).getJson())

        ).andReturn().getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    @DisplayName("Ao deletar com cultura na existe ou produto nao relacionado com a cultura")
    public void deleteCultureIdCase2() throws Exception {
        UUID cultureId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(cultureService.delete(any())).thenReturn(false);
        ProductCultureDeleteForm form = new ProductCultureDeleteForm();
        form.setCultureId(cultureId);
        form.setProductId(productId);

        var response = mockMvc.perform(
                delete("/products/cultures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productCultureDeleteFormJson.write(form).getJson())

        ).andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    @DisplayName("Ao deletar com campos faltante ou em formato incorreto deve retornar Bad request")
    public void deleteCultureIdCase3() throws Exception {
        ProductCultureDeleteForm form = new ProductCultureDeleteForm();
        var response = mockMvc.perform(
                delete("/products/cultures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productCultureDeleteFormJson.write(form).getJson())

        ).andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @DisplayName("nao permitido deletar cultura do produto")
    public void deleteCultureIdCase4() throws Exception {
        UUID cultureId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        when(cultureService.delete(any())).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
        ProductCultureDeleteForm form = new ProductCultureDeleteForm();
        form.setCultureId(cultureId);
        form.setProductId(productId);

        var response = mockMvc.perform(
                delete("/products/cultures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productCultureDeleteFormJson.write(form).getJson())

        ).andReturn().getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }
}
