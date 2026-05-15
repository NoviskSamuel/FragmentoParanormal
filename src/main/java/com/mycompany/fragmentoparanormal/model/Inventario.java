package com.mycompany.fragmentoparanormal.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Inventario {
    private List<Item> itens = new ArrayList<>();
    private Item itemEquipado;

    public void adicionarItem(Item item) {
        itens.add(item);
    }

    public void removerItem(Item item) {
        itens.remove(item);
    }

    public void equiparItem(Item item) {
        if (itens.contains(item)) this.itemEquipado = item;
    }

    public Item getItemEquipado() { return itemEquipado; }

    public List<Item> getItens() { return itens; }

    public List<Item> getItensPorTipo(String tipo) {
        return itens.stream()
                .filter(i -> i.getTipo().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());
    }

    public int contarFragmentos() {
        return (int) itens.stream()
                .filter(i -> i.getTipo().equalsIgnoreCase("FRAGMENTO"))
                .count();
    }

    public void limpar() {
        itens.clear();
        itemEquipado = null;
    }
}