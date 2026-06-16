package br.uel.restaurante;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ItemPedidoController {
    private static final String SESSION_PEDIDO = "sessionPedido";

    @Autowired
    private ItemCardapioRepository itemCardapioRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @GetMapping("/pedido")
    public String mostrarPedido(Model model, HttpServletRequest request) {
        List<ItemPedido> sessionPedido = (List<ItemPedido>) request.getSession().getAttribute(SESSION_PEDIDO);
        double precoTotal;

        // Verifica se há itens no carrinho de pedidos
        if (CollectionUtils.isEmpty(sessionPedido)) {
            model.addAttribute("carrinhoVazio", true);
        } else {
            List<ItemPedido> itensPedidoExistentes = new ArrayList<>();
            double novoPrecoTotal = 0.0;
            for (ItemPedido itemPedido : sessionPedido) {
                ItemCardapio itemCardapio = itemCardapioRepository.findById(itemPedido.getId()).orElse(null);
                if (itemCardapio != null) {
                    // Criar um novo ItemPedido com os dados atualizados do ItemCardapio
                    ItemPedido itemPedidoAtualizado = new ItemPedido();
                    itemPedidoAtualizado.setId(itemPedido.getId());
                    itemPedidoAtualizado.setQuantidade(itemPedido.getQuantidade());
                    itemPedidoAtualizado.setNome(itemCardapio.getNome());
                    itemPedidoAtualizado.setDescricao(itemCardapio.getRestaurante().getNome()); // Usar o nome do restaurante
                    itemPedidoAtualizado.setPreco(itemCardapio.getPreco());

                    itensPedidoExistentes.add(itemPedidoAtualizado);
                    novoPrecoTotal += itemPedidoAtualizado.getQuantidade() * itemPedidoAtualizado.getPreco();
                }
            }

            precoTotal = novoPrecoTotal;

            sessionPedido.retainAll(itensPedidoExistentes);

            if (!itensPedidoExistentes.isEmpty()) {
                model.addAttribute("carrinhoVazio", false);
                model.addAttribute("sessionPedido", itensPedidoExistentes);
                model.addAttribute("precoTotal", precoTotal);
            } else {
                request.getSession().removeAttribute(SESSION_PEDIDO);
                model.addAttribute("carrinhoVazio", true);
            }
        }

        return "pedido";
    }





    @GetMapping("/adicionar-pedido/{id}")
    public String adicionarPedido(@PathVariable("id") int id, Model model, HttpServletRequest request) {
        List<ItemPedido> sessionPedido = (List<ItemPedido>) request.getSession().getAttribute(SESSION_PEDIDO);

        if (sessionPedido == null) {
            sessionPedido = new ArrayList<>();
            request.getSession().setAttribute(SESSION_PEDIDO, sessionPedido);
        }

        ItemCardapio itemCardapio = itemCardapioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("O id do produto é inválido: " + id));

        ItemPedido itemAdicionado = sessionPedido.stream()
                .filter(p -> p.getId() == itemCardapio.getId())
                .findFirst()
                .orElse(null);

        if (itemAdicionado != null) {
            itemAdicionado.setQuantidade(itemAdicionado.getQuantidade() + 1);
        } else {
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setId(itemCardapio.getId());
            itemPedido.setNome(itemCardapio.getNome());
            itemPedido.setDescricao(itemCardapio.getDescricao());
            itemPedido.setPreco(itemCardapio.getPreco());
            itemPedido.setQuantidade(1);
            sessionPedido.add(itemPedido);
        }

        request.getSession().setAttribute(SESSION_PEDIDO, sessionPedido);

        return "redirect:/pedido";
    }

    @GetMapping("/pedido/remover/{id}")
    public String removerPedido(@PathVariable("id") int id, HttpServletRequest request) {
        List<ItemPedido> sessionPedido = (List<ItemPedido>) request.getSession().getAttribute(SESSION_PEDIDO);
        ItemCardapio itemCardapio = itemCardapioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("O id do produto é inválido: " + id));

        ItemPedido itemAdicionado = sessionPedido.stream()
                .filter(p -> p.getId() == itemCardapio.getId())
                .findFirst()
                .orElse(null);

        sessionPedido.remove(itemAdicionado);
        request.getSession().setAttribute(SESSION_PEDIDO, sessionPedido);

        return "redirect:/pedido";
    }

    @GetMapping("/pedido/somar/{id}")
    public String somarPedido(@PathVariable("id") int id, HttpServletRequest request) {
        List<ItemPedido> sessionPedido = (List<ItemPedido>) request.getSession().getAttribute(SESSION_PEDIDO);
        ItemCardapio itemCardapio = itemCardapioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("O id do produto é inválido: " + id));

        ItemPedido itemAdicionado = sessionPedido.stream()
                .filter(p -> p.getId() == itemCardapio.getId())
                .findFirst()
                .orElse(null);

        itemAdicionado.setQuantidade(itemAdicionado.getQuantidade() + 1);
        request.getSession().setAttribute(SESSION_PEDIDO, sessionPedido);

        return "redirect:/pedido";
    }

    @GetMapping("/pedido/subtrair/{id}")
    public String subtrairPedido(@PathVariable("id") int id, HttpServletRequest request) {
        List<ItemPedido> sessionPedido = (List<ItemPedido>) request.getSession().getAttribute(SESSION_PEDIDO);
        ItemCardapio itemCardapio = itemCardapioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("O id do produto é inválido: " + id));

        ItemPedido itemAdicionado = sessionPedido.stream()
                .filter(p -> p.getId() == itemCardapio.getId())
                .findFirst()
                .orElse(null);

        if (itemAdicionado != null) {
            int quantidadeNova = itemAdicionado.getQuantidade() - 1;
            if (quantidadeNova > 0) {
                itemAdicionado.setQuantidade(quantidadeNova);
            } else {
                sessionPedido.remove(itemAdicionado);
            }
        }

        request.getSession().setAttribute(SESSION_PEDIDO, sessionPedido);

        return "redirect:/pedido";
    }


}
