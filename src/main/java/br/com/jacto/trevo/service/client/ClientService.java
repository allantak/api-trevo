package br.com.jacto.trevo.service.client;

import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<Client> getAll(){
        List<Client> resultClients = (List<Client>) clientRepository.findAll();
        return resultClients.stream().toList();
    }

    public Client create(Client client ) {
        return clientRepository.save(client);
    }

    public Client getId(UUID id){
        return clientRepository.getReferenceById(id);
    }

    public void delete(UUID id){
        clientRepository.deleteById(id);
    }

}
