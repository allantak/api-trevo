package br.com.jacto.trevo;

import br.com.jacto.trevo.models.entities.Client;
import br.com.jacto.trevo.repositorys.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TrevoApplication implements CommandLineRunner {

	private ClientRepository repositoryClient;
	public TrevoApplication(ClientRepository repositoryClient){

		this.repositoryClient = repositoryClient;
	}
	public static void main(String[] args) {
		SpringApplication.run(TrevoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Client client = new Client();
		client.setClient_name("Ronaldo");
		client.setEmail("ronaldinho@gmail.com");
		client.setPhone("(14) 99876-7439");

		repositoryClient.save(client);
	}
}
