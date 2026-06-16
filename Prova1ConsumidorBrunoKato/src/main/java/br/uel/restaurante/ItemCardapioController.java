package br.uel.restaurante;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ItemCardapioController {

    @Autowired
    private ItemCardapioRepository itemCardapioRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @GetMapping("/cardapio/{id_restaurante}")
    public String mostrarListaCardapios(@PathVariable("id_restaurante") int idRestaurante, Model model) {
        Restaurante restaurante = restauranteRepository.findById(idRestaurante)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado com o id: " + idRestaurante));

        List<ItemCardapio> itemCardapios = itemCardapioRepository.findByRestaurante(restaurante);

        if (!itemCardapios.isEmpty()) {
            model.addAttribute("itemCardapios", itemCardapios);
        } else {
            model.addAttribute("itemCardapios", null);
        }

        model.addAttribute("restaurante", restaurante);

        return "cardapio";
    }



}