package br.com.jacto.trevo.controller.product;

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
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
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

@RunWith(SpringRunner.class)
@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
public class PublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ProductService productService;

    @MockBean
    CultureService cultureService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private AccountRepository managerRepository;

    @Autowired
    private JacksonTester<PageImpl<ProductDto>> productDtoJson;
    @Autowired
    private JacksonTester<ProductDetailDto> productDetailDtoJson;
    @Autowired
    private JacksonTester<ProductOrderDto> productOrderDtoJson;
    @Autowired
    private JacksonTester<ProductCreateDto> productCreateDtoJson;
    @Autowired
    private JacksonTester<ProductForm> productFormJson;
    @Autowired
    private JacksonTester<ProductCultureForm> productCultureFormJson;
    @Autowired
    private JacksonTester<ProductUpdateForm> productUpdateFormJson;
    @Autowired
    private JacksonTester<Culture> cultureJson;
    @Autowired
    private JacksonTester<Product> productJson;
    @Autowired
    private JacksonTester<ProductCultureDeleteForm> productCultureDeleteFormJson;

    @Autowired
    private JacksonTester<ProductCultureDto> productCultureDtoJson;


    public Account account = new Account("test", "12345", "test", Account.Role.COLABORADOR);
    public Product product = new Product("Trator Jacto", Product.Status.DISPONIVEL, Product.Category.ELETRICO, "Trator jacto para agricultura", 120.0, 2.0, LocalDateTime.of(2023, 3, 28, 10, 30, 15, 500000000), account);

    public OrderItem order = new OrderItem(3, account, product);
    public Culture culture = new Culture("Cerejeiras", product);


    @Test
    @DisplayName("Productos listado com detalhes da paginacao")
    public void listProduct() throws Exception {
        UUID productId = UUID.randomUUID();
        product.setProductId(productId);
        List<Culture> listClient = new ArrayList<Culture>();
        listClient.add(culture);
        product.setCultures(listClient);

        List<ProductDto> productDto = new ArrayList<ProductDto>();
        productDto.add(new ProductDto(product));

        PageImpl<ProductDto> page = new PageImpl<>(productDto);

        when(productService.getAll(any(Pageable.class))).thenReturn(page);

        var response = mockMvc.perform(
                        get("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = productDtoJson.write(page).getJson();


        assertEquals(jsonExpect, response.getContentAsString());

    }


    @Test
    @DisplayName("Especifico producto detalhado")
    public void productId() throws Exception {
        UUID productId = UUID.randomUUID();
        product.setProductId(productId);

        List<Culture> listClient = new ArrayList<Culture>();
        listClient.add(culture);
        product.setCultures(listClient);

        List<OrderItem> orderItems = new ArrayList<OrderItem>();
        orderItems.add(order);
        product.setOrders(orderItems);

        ProductDetailDto productDetail = new ProductDetailDto(product);

        when(productService.getId(any())).thenReturn(Optional.of(productDetail));

        var response = mockMvc.perform(
                        get("/products/" + product.getProductId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var jsonExpect = productDetailDtoJson.write(productDetail).getJson();

        assertEquals(jsonExpect, response.getContentAsString());

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
