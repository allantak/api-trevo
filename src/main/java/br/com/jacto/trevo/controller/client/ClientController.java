package br.com.jacto.trevo.controller.client;

import br.com.jacto.trevo.controller.client.dto.ClientDetailDto;
import br.com.jacto.trevo.controller.client.dto.ClientDto;
import br.com.jacto.trevo.controller.client.dto.ClientOrderDto;
import br.com.jacto.trevo.controller.client.form.ClientForm;
import br.com.jacto.trevo.controller.client.form.ClientUpdateForm;
import br.com.jacto.trevo.service.client.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
@Tag(name = "Cliente", description = "Gerenciamento de clientes")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping
    @Operation(summary = "Listagem de clientes registrados")
    @SecurityRequirement(name = "bearer-key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ClientDto.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public List<ClientDto> getClient() {
        return clientService.getAll();
    }

    @GetMapping("/orders/{id}")
    @Operation(summary = "Mostra os pedidos do cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientOrderDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<ClientOrderDto> getClientOrder(@PathVariable UUID id) {
        Optional<ClientOrderDto> clientOrder = clientService.clientOrder(id);
        return clientOrder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Mostra os detalhes do cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientDetailDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<ClientDetailDto> getClientId(@PathVariable UUID id) {
        Optional<ClientDetailDto> client = clientService.getId(id);
        return client.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Registra o cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<ClientDto> createClient(@RequestBody @Valid ClientForm client, UriComponentsBuilder uriBuilder) {
        ClientDto save = clientService.create(client);
        URI uri = uriBuilder.path("/clients/{id}").buildAndExpand(save.getClientId()).toUri();
        return ResponseEntity.created(uri).body(save);
    }

    @PutMapping
    @Transactional
    @Operation(summary = "Atualiza o cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientDetailDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<ClientDetailDto> updateClient(@RequestBody @Valid ClientUpdateForm client) {
        Optional<ClientDetailDto> updateClient = clientService.update(client);
        return updateClient.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Delete cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success no-content", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        Boolean findClient = clientService.delete(id);
        return findClient ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
