package br.com.jacto.trevo.controller.client;

import br.com.jacto.trevo.controller.client.dto.ClientDetailDto;
import br.com.jacto.trevo.controller.client.dto.ClientDto;
import br.com.jacto.trevo.controller.client.dto.ClientOrderDto;
import br.com.jacto.trevo.controller.client.form.ClientForm;
import br.com.jacto.trevo.controller.client.form.ClientUpdateForm;
import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.service.client.ClientService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping
    public List<ClientDto> getClient() {
        return clientService.getAll();
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Optional<ClientOrderDto>> getClientOrder(@PathVariable UUID id) {
        Optional<ClientOrderDto> clientOrder = clientService.clientOrder(id);
        if (clientOrder.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(clientOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<ClientDetailDto>> getClientId(@PathVariable UUID id) {
        Optional<ClientDetailDto> client = clientService.getId(id);
        if (client.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ;

        return ResponseEntity.ok(client);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ClientDto> createClient(@RequestBody @Valid ClientForm client, UriComponentsBuilder uriBuilder) {
        Client save = clientService.create(client);
        URI uri = uriBuilder.path("/clients/{id}").buildAndExpand(save.getId()).toUri();
        return ResponseEntity.created(uri).body(new ClientDto(save));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Optional<ClientDetailDto>> updateClient(@RequestBody @Valid ClientUpdateForm client) {
        Optional<Client> updateClient = clientService.update(client);
        if (updateClient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updateClient.map(ClientDetailDto::new));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Client> deleteClient(@PathVariable UUID id) {
        Optional<ClientDetailDto> findClient = clientService.getId(id);
        if (findClient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
