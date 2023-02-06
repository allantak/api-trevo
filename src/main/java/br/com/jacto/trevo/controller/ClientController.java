package br.com.jacto.trevo.controller;

import br.com.jacto.trevo.controller.dto.ClientDto;
import br.com.jacto.trevo.controller.form.ClientForm;
import br.com.jacto.trevo.controller.form.ClientUpdateForm;
import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.service.client.ClientService;
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
    public ResponseEntity<Optional<Client>> getClientId(@PathVariable UUID id) {
        Optional<Client> client = clientService.getId(id);
        if(client.isEmpty()){
            return ResponseEntity.notFound().build();
        } ;
        return ResponseEntity.ok(client);
    }

    @PostMapping
    public ResponseEntity<ClientDto> createClient(@RequestBody @Valid ClientForm client, UriComponentsBuilder uriBuilder) {
        Client save = clientService.create(client);
        URI uri = uriBuilder.path("/clients/{id}").buildAndExpand(save.getId()).toUri();
        return ResponseEntity.created(uri).body(new ClientDto(save));
    }

    @PutMapping
    public ResponseEntity<Optional<Client>> updateClient(@RequestBody @Valid ClientUpdateForm client){
        Optional<Client> updateClient = clientService.update(client);
        if( updateClient.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(200).body(updateClient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Client> deleteClient(@PathVariable UUID id){
        Optional<Client> findClient = clientService.getId(id);
        if( findClient.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
