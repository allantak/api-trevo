package br.com.jacto.trevo.repository;

import br.com.jacto.trevo.model.product.Culture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CultureRepository extends JpaRepository<Culture, UUID> {
}
