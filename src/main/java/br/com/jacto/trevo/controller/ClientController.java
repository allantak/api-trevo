package br.com.jacto.trevo.controller;

import br.com.jacto.trevo.controller.dto.ClientDto;
import br.com.jacto.trevo.controller.form.ClientForm;
import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.service.client.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping
    public List<Client> getClient(){
        return clientService.getAll();
    }

    @PostMapping
    public ResponseEntity<ClientDto> createClient(@RequestBody @Valid ClientForm client, UriComponentsBuilder uriBuilder) {
        Client register = new Client( client.getClientName(), client.getEmail(), client.getPhone());
        Client save = clientService.create(register);

        // UriBuilder um metodo capaz de pegar a raiz da url
        URI uri = uriBuilder.path("/clients/{id}").buildAndExpand(save.getId()).toUri();

        // ReponseEntity.created retorna o status 201 com a uri do registro e objeto criado
        return ResponseEntity.created(uri).body(new ClientDto(save));
    }
}
