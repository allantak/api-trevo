package br.com.jacto.trevo.repositorys;

import br.com.jacto.trevo.models.entities.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClientRepository extends CrudRepository<Client, UUID> {

}
