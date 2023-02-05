package br.com.jacto.trevo.service.client;

import br.com.jacto.trevo.controller.form.ClientForm;
import br.com.jacto.trevo.controller.form.ClientUpdateForm;
import br.com.jacto.trevo.model.client.Client;
import br.com.jacto.trevo.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<Client> getAll() {
        List<Client> resultClients = (List<Client>) clientRepository.findAll();
        return resultClients.stream().toList();
    }

    public Client create(ClientForm client) {
        Client register = new Client(client.getClientName(), client.getEmail(), client.getPhone());
        return clientRepository.save(register);
    }

    public Optional<Client> getId(UUID id) {
        System.out.println("asdasdasdasd aqqui " + id);
        return clientRepository.findById(id);
    }

    public void delete(UUID id) {
        clientRepository.deleteById(id);
    }

    public Client update(ClientUpdateForm client) {

        Client updateClient = new Client();
        updateClient.setClientId(client.getClientId());

        if (client.getEmail() != null) {
            updateClient.setEmail(client.getEmail());
        }

        if (client.getClientName() != null) {
            updateClient.setClientName(client.getClientName());
        }

        if (client.getPhone() != null) {
            updateClient.setPhone(client.getPhone());
        }

        return clientRepository.save(updateClient);

    }

}
