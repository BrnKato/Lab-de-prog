package br.uel.restaurante;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;


@Controller
public class RestauranteController {

    @Autowired
    RestauranteRepository restauranteRepository;

    @GetMapping(value = {"/index", "/"})
    public String mostrarListaRestaurante(Model model) {
        Iterable<Restaurante> restaurantesIterable = restauranteRepository.findAll();
        List<Restaurante> restaurantes = new ArrayList<>();
        restaurantesIterable.forEach(restaurantes::add);

        if (restaurantes.isEmpty()) {
            model.addAttribute("mensagem", "Não há restaurantes registrados.");
        } else {
            model.addAttribute("restaurantes", restaurantes);
        }
        return "index";
    }

}