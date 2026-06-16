package br.uel.restaurante;

import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RestauranteRepository extends CrudRepository<Restaurante, Integer> {

    Optional<Restaurante> findByNome(String nome);
}
