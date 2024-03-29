package br.com.jacto.trevo.controller.auth;

import br.com.jacto.trevo.config.exception.dto.Error403;
import br.com.jacto.trevo.config.security.TokenService;
import br.com.jacto.trevo.controller.auth.dto.*;
import br.com.jacto.trevo.controller.auth.form.AccountLoginForm;
import br.com.jacto.trevo.controller.auth.form.AccountRegisterForm;
import br.com.jacto.trevo.controller.auth.form.AccountUpdateForm;
import br.com.jacto.trevo.model.account.Account;
import br.com.jacto.trevo.service.account.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@Tag(name = "Autenticar", description = "Gerenciamento de usuário")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager account;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TokenService tokenService;

    @GetMapping("/accounts")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Lista de usuário - ADMINISTRADOR", description = "Apenas ADMINISTRADOR")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AccountDto.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public List<AccountDto> getAccount() {
        return accountService.getAll();
    }

    @GetMapping("/accounts/{id}")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Mostrar detalhes do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDetailDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<AccountDetailDto> getIdAccount(@PathVariable UUID id) {
        Optional<AccountDetailDto> findAccount = accountService.findAccount(id);
        return findAccount.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/accounts/orders/{id}")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Mostrar pedidos feito pelo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountOrderDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<AccountOrderDto> getIdAccountOrder(@PathVariable UUID id) {
        Optional<AccountOrderDto> findAccount = accountService.findAccountOrder(id);
        return findAccount.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/login")
    @Operation(summary = "Autenticar o usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<TokenDto> authAccount(@RequestBody @Valid AccountLoginForm user) {
        Authentication verify = accountService.auth(user);
        Authentication authentication = account.authenticate(verify);
        TokenDto token = tokenService.token((Account) authentication.getPrincipal());
        return ResponseEntity.ok(token);

    }

    @PostMapping("/register")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Registrar usuario", description = "Registro de ADMINISTRADOR apenas ADMINISTRADOR tem acesso. Registro de COLABORADOR apenas ADMINISTRADOR ou COLABORADOR tem acesso. Cliente tem acesso para todos. accountRole = ADMINISTRADOR, COLABORADOR ou CLIENTE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<?> createAccount(@RequestBody @Valid AccountRegisterForm user) throws AccessDeniedException {
        try {
            AccountDto accountDto = accountService.createAccount(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(accountDto);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Error403(e));
        }
    }

    @PutMapping("/accounts")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Atualizar usuário", description = "Atualizar ADMINISTRADOR apenas ADMINISTRADOR tem acesso. Atualizar COLABORADOR apenas ADMINISTRADOR ou COLABORADOR tem acesso. Cliente tem acesso para todos. accountRole = ADMINISTRADOR, COLABORADOR ou CLIENTE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<?> updateAccount(@RequestBody @Valid AccountUpdateForm user) throws AccessDeniedException {
        try {
            Optional<AccountCreateDto> account = accountService.updateAccount(user);
            return account.map(value -> ResponseEntity.status(HttpStatus.OK).body(value)).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Error403(e));
        }
    }

    @DeleteMapping("/accounts/{id}")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Delete usuário", description = "Deletar ADMINISTRADOR apenas ADMINISTRADOR tem acesso. Deletar COLABORADOR apenas ADMINISTRADOR ou COLABORADOR tem acesso. Cliente tem acesso para todos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success no-content", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<?> deleteAccount(@PathVariable UUID id) throws AccessDeniedException {
        try {
            boolean accountDelete = accountService.delete(id);
            return accountDelete ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Error403(e));
        }
    }
}
