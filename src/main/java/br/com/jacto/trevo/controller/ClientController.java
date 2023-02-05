package br.com.jacto.trevo.controller;

import br.com.jacto.trevo.controller.dto.ClientDto;
import br.com.jacto.trevo.controller.form.ClientForm;
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
    public List<Client> getClient() {
        return clientService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Client> getClientId(@PathVariable UUID id) {
        return clientService.getId(id);
    }

    @PostMapping
    public ResponseEntity<ClientDto> createClient(@RequestBody @Valid ClientForm client, UriComponentsBuilder uriBuilder) {
        Client save = clientService.create(client);
        URI uri = uriBuilder.path("/clients/{id}").buildAndExpand(save.getId()).toUri();
        return ResponseEntity.created(uri).body(new ClientDto(save));
    }
}
