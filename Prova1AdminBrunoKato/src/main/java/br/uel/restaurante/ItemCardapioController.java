package br.uel.restaurante;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ItemCardapioController {

    @Autowired
    private ItemCardapioRepository itemCardapioRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @GetMapping("/novo-item-cardapio/{id_restaurante}")
    public String mostrarFormNovoCardapio(@PathVariable("id_restaurante") int idRestaurante, Model model) {
        model.addAttribute("id_restaurante", idRestaurante);
        model.addAttribute("itemCardapio", new ItemCardapio());
        return "novo-item-cardapio";
    }



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


    @PostMapping("/adicionar-item-cardapio/{id_restaurante}")
    public String adicionarItemCardapio(@Valid ItemCardapio itemCardapio, BindingResult result, @PathVariable("id_restaurante") int idRestaurante) {
        if (result.hasErrors()) {
            return "/novo-item-cardapio";
        }

        Restaurante restaurante = restauranteRepository.findById(idRestaurante)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado com o id: " + idRestaurante));

        itemCardapio.setRestaurante(restaurante);

        itemCardapioRepository.save(itemCardapio);

        return "redirect:/cardapio/" + idRestaurante;
    }

    @GetMapping("/remover-item-cardapio/{id_restaurante}/{id}")
    public String removerItemCardapio(@PathVariable("id_restaurante") int idRestaurante,  @PathVariable("id") int idItemCardapio) {

        ItemCardapio itemCardapio= itemCardapioRepository.findById(idItemCardapio)
                .orElseThrow(() -> new IllegalArgumentException("ItemCardapio não encontrado com o id: " + idItemCardapio));


        itemCardapioRepository.delete(itemCardapio);

        return "redirect:/cardapio/" + idRestaurante;
    }

    @GetMapping("/editar-item-cardapio/{id_restaurante}/{id}")
    public String mostrarFormAtualizarItemCardapio(@PathVariable("id_restaurante") int idRestaurante, @PathVariable("id") int idItemCardapio, Model model) {
        ItemCardapio itemCardapio = itemCardapioRepository.findById(idItemCardapio)
                .orElseThrow(() -> new IllegalArgumentException(
                        "O id do itemCardapio é inválido:" + idItemCardapio));

        Restaurante restaurante = restauranteRepository.findById(idRestaurante)
                .orElseThrow(() -> new IllegalArgumentException(
                        "O id do restaurante é inválido:" + idRestaurante));

        model.addAttribute("restaurante", restaurante);
        model.addAttribute("itemCardapio", itemCardapio);
        return "atualizar-item-cardapio";
    }

    @PostMapping("/atualizar-item-cardapio/{id_restaurante}/{id}")
    public String atualizarRestaurante(@PathVariable("id_restaurante") int idRestaurante, @PathVariable("id") int id, @Valid ItemCardapio itemCardapio,
                                       BindingResult result, Model model) {
        if (result.hasErrors()) {
            Restaurante restaurante = restauranteRepository.findById(idRestaurante)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "O id do restaurante é inválido:" + idRestaurante));

            model.addAttribute("restaurante", restaurante);
            model.addAttribute("itemCardapio", itemCardapio);

            return "atualizar-item-cardapio";
        }

        ItemCardapio itemCardapioRegistrado = itemCardapioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("O id do itemCardapio é inválido:" + id));

        itemCardapioRegistrado.setNome(itemCardapio.getNome());
        itemCardapioRegistrado.setDescricao(itemCardapio.getDescricao());
        itemCardapioRegistrado.setPreco(itemCardapio.getPreco());

        itemCardapioRepository.save(itemCardapioRegistrado);

        return "redirect:/cardapio/" + idRestaurante;
    }




}





