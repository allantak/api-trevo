package br.com.jacto.trevo.controller.auth;

import br.com.jacto.trevo.config.security.TokenService;
import br.com.jacto.trevo.controller.auth.dto.AccountCreateDto;
import br.com.jacto.trevo.controller.auth.dto.AccountDto;
import br.com.jacto.trevo.controller.auth.dto.TokenDto;
import br.com.jacto.trevo.controller.auth.form.AccountForm;
import br.com.jacto.trevo.controller.auth.form.AccountUpdateForm;
import br.com.jacto.trevo.model.account.Account;
import br.com.jacto.trevo.service.manager.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@Controller
@Tag(name = "Autenticar", description = "Gerenciamento de usuário")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager account;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TokenService tokenService;


    @PostMapping("/login")
    @Operation(summary = "Autenticar o usuário", description = "login da conta do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<TokenDto> authAccount(@RequestBody @Valid AccountForm user) {
        Authentication verify = accountService.auth(user);
        Authentication authentication = account.authenticate(verify);
        TokenDto token = tokenService.token((Account) authentication.getPrincipal());
        return ResponseEntity.ok(token);

    }

    @PostMapping("/register")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Registrar usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<AccountDto> createAccount(@RequestBody @Valid AccountForm user) {
        AccountDto accountDto = accountService.createAccount(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountDto);
    }

    @PutMapping("/account")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Atualizar usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<AccountCreateDto> updateAccount(@RequestBody @Valid AccountUpdateForm user) {
        Optional<AccountCreateDto> account = accountService.updateAccount(user);
        return account.map(value -> ResponseEntity.status(HttpStatus.OK).body(value)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/account/{id}")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Delete usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success no-content", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
        boolean accountDelete = accountService.delete(id);
        return accountDelete ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
