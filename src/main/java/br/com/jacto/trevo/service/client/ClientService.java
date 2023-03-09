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

    public ClientDto create(ClientForm client) {
        Client register = new Client(client.getClientName(), client.getEmail(), client.getPhone());
        Client convert = clientRepository.save(register);
        return new ClientDto(convert);
    }

    public Optional<ClientDetailDto> getEmail(String email) {
        return clientRepository.findByEmail(email).map(ClientDetailDto::new);
    }

    public Boolean delete(UUID id) {
        Optional<Client> findClient = clientRepository.findById(id);
        if (findClient.isEmpty()) {
            return false;
        }
        clientRepository.deleteById(id);
        return true;
    }

    public Optional<ClientDetailDto> update(ClientUpdateForm client) {

        Optional<Client> findClient = clientRepository.findById(client.getClientId());

        if (findClient.isEmpty()) return Optional.empty();

        if (client.getEmail() != null && !client.getEmail().trim().isEmpty()) {
            findClient.get().setEmail(client.getEmail());
        }

        if (client.getClientName() != null && !client.getClientName().trim().isEmpty()) {
            findClient.get().setClientName(client.getClientName());
        }

        if (client.getPhone() != null && !client.getPhone().trim().isEmpty()) {
            findClient.get().setPhone(client.getPhone());
        }

        return Optional.of(clientRepository.save(findClient.get())).map(ClientDetailDto::new);

    }

    public Optional<ClientOrderDto> clientOrder(UUID clientId) {
        return clientRepository.findById(clientId).map(ClientOrderDto::new);
    }

}
