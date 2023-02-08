package br.com.jacto.trevo.service.client;

import br.com.jacto.trevo.controller.client.dto.ClientDetailDto;
import br.com.jacto.trevo.controller.client.dto.ClientDto;
import br.com.jacto.trevo.controller.client.dto.ClientOrderDto;
import br.com.jacto.trevo.controller.client.form.ClientForm;
import br.com.jacto.trevo.controller.client.form.ClientUpdateForm;
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

    public List<ClientDto> getAll() {
        return clientRepository.findAll().stream().map(ClientDto::new).toList();
    }

    public Client create(ClientForm client) {
        Client register = new Client(client.getClientName(), client.getEmail(), client.getPhone());
        return clientRepository.save(register);
    }

    public Optional<ClientDetailDto> getId(UUID id) {
        return clientRepository.findById(id).map(ClientDetailDto::new);
    }

    public void delete(UUID id) {
        clientRepository.deleteById(id);
    }

    public Optional<Client> update(ClientUpdateForm client) {

        Optional<Client> findClient = clientRepository.findById(client.getClientId());

        if (findClient.isEmpty()) return findClient;

        if (client.getEmail() != null && !client.getEmail().trim().isEmpty()) {
            findClient.get().setEmail(client.getEmail());
        }

        if (client.getClientName() != null && !client.getClientName().trim().isEmpty()) {
            findClient.get().setClientName(client.getClientName());
        }

        if (client.getPhone() != null && !client.getPhone().trim().isEmpty()) {
            findClient.get().setPhone(client.getPhone());
        }

        return Optional.of(clientRepository.save(findClient.get()));

    }

    public Optional<ClientOrderDto> clientOrder(UUID clientId){
        return clientRepository.findById(clientId).map(ClientOrderDto::new);
    }

}
