package me.dio.sacola.service.impl;

import lombok.RequiredArgsConstructor;
import me.dio.sacola.enumeration.FormaPagamento;
import me.dio.sacola.model.Item;
import me.dio.sacola.model.Restaurante;
import me.dio.sacola.model.Sacola;
import me.dio.sacola.repository.ItemRepository;
import me.dio.sacola.repository.ProdutoRepository;
import me.dio.sacola.repository.SacolaRepository;
import me.dio.sacola.resource.dto.ItemDto;
import me.dio.sacola.service.SacolaService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SacolaServiceImpl implements SacolaService {

    private final SacolaRepository sacolaRepository;
    private final ProdutoRepository produtoRepository;

    private final ItemRepository itemRepository;

    @Override
    public Item incluirItemNaSacola(ItemDto itemDto) {
        Sacola sacola = verSacola(itemDto.getSacolaId());

        if (sacola.getFechada()){
            throw new RuntimeException("Esta sacola esta fechada");
        }

        Item itemInserido = Item.builder()
            .quantidade(itemDto.getQuantidade())
            .sacola(sacola)
            .produto(produtoRepository.findById(itemDto.getProdutoId()).orElseThrow(
                    () -> {
                        throw new RuntimeException("Esse produto não existe");
                    }
            ))
            .build();

        List<Item> itens = sacola.getItens();
        if (itens.isEmpty()){
            itens.add(itemInserido);
        } else {
            Restaurante restauranteAtual = itens.get(0).getProduto().getRestaurante();
            Restaurante restauranteDoProduto = itemInserido.getProduto().getRestaurante();
            if (restauranteAtual.equals(restauranteDoProduto)){
                itens.add(itemInserido);
            } else {
                throw new RuntimeException("Não é possivel adicionar produtos de restaurantes diferentes. Feche a sacola ou esvazie.");
            }
        }


        List<Double> valoresItens = new ArrayList<>();
        for (Item itemSacola : itens) {
            double valorTotalItem = itemSacola.getProduto().getValorUnitario() * itemSacola.getQuantidade();
            valoresItens.add(valorTotalItem);
        }

        double valorTotalSacola = valoresItens.stream().mapToDouble(valorTotalDeCadaItem -> valorTotalDeCadaItem).sum();

        sacola.setValorTotal(valorTotalSacola);
        sacolaRepository.save(sacola);
        return itemInserido;
    }

    @Override
    public Sacola verSacola(Long id) {
        return sacolaRepository.findById(id).orElseThrow(
                () -> {
                    throw new RuntimeException("Essa sacola não existe");
                }
        );
    }

    @Override
    public Sacola fecharSacola(Long id, int formaPagamento) {
        Sacola sacola = verSacola(id);
        FormaPagamento formaPag = formaPagamento == 0 ? FormaPagamento.DINHEIRO : FormaPagamento.MAQUINETA;


        if (sacola.getItens().isEmpty()) {
            throw new RuntimeException("Inclua itens na sacola");
        }

        sacola.setFormaPagamento(formaPag);
        sacola.setFechada(true);

        return sacolaRepository.save(sacola);
    }
}
