package br.com.jacto.trevo.controller;

import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping(value = "/client", method = RequestMethod.GET)
    public List<Client> getClients(){
        List<Client> resultClients = (List<Client>) clientRepository.findAll();
        return resultClients.stream().toList();
    }

}
