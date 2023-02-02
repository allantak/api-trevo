package br.com.jacto.trevo.controller;

import br.com.jacto.trevo.controller.form.ClientForm;
import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping
    public List<Client> getClients(){
        List<Client> resultClients = (List<Client>) clientRepository.findAll();
        return resultClients.stream().toList();
    }

    @PostMapping
    public void createClient(@RequestBody ClientForm client) {
        Client register = new Client( client.getClientName(), client.getEmail(), client.getPhone());
        clientRepository.save(register);
    }

}
