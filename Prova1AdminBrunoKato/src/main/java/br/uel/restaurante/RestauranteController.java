package br.uel.restaurante;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
public class RestauranteController {

    @Autowired
    RestauranteRepository restauranteRepository;

    @GetMapping("/novo-restaurante")
    public String mostrarFormNovoRestaurante(Restaurante restaurante) {
        return "novo-restaurante";
    }


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

    @PostMapping("/adicionar-restaurante")
    public String adicionarRestaurante(@Valid Restaurante restaurante, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "/novo-restaurante";
        }

        Optional<Restaurante> existente = restauranteRepository.findByNome(restaurante.getNome());
        if (existente.isPresent()) {
            model.addAttribute("erroNomeExiste", "Já existe um restaurante com este nome.");
            return "redirect:/index";
        }

        restauranteRepository.save(restaurante);
        return "redirect:/index";
    }


    @GetMapping("/remover/{id}")
    public String removerRestaurante(@PathVariable("id") int id, HttpServletRequest request) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("O id do restaurante é inválido:" + id));
        restauranteRepository.delete(restaurante);

        return "redirect:/index";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormAtualizar(@PathVariable("id") int id, Model model) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "O id do restaurante é inválido:" + id));


        model.addAttribute("restaurante", restaurante);
        return "atualizar-restaurante";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarRestaurante(@PathVariable("id") int id, @Valid Restaurante restaurante,
                                       BindingResult result, Model model) {
        if (result.hasErrors()) {
            restaurante.setId(id);
            return "atualizar-restaurante";
        }

        Restaurante restauranteRegistrado = restauranteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("O id do restaurante é inválido:" + id));

        restauranteRegistrado.setNome(restaurante.getNome());

        restauranteRepository.save(restauranteRegistrado);

        return "redirect:/index";
    }

}