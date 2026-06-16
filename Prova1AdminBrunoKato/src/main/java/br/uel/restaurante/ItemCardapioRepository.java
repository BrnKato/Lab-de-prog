package br.uel.restaurante;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemCardapioRepository extends JpaRepository<ItemCardapio, Integer> {

    List<ItemCardapio> findByRestaurante(Restaurante restaurante);
}
