package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Inventario {

    private final List<Item> itens = new ArrayList<>();
    private Item itemEquipado;

    public void adicionarItem(Item item) {
        if (item != null) itens.add(item);
    }

    public void removerItem(Item item) {
        itens.remove(item);
        if (item != null && item.equals(itemEquipado)) {
            itemEquipado = null;
        }
    }

    public boolean equiparItem(Item item) {
        if (item != null && itens.contains(item)) {
            itemEquipado = item;
            return true;
        }
        return false;
    }

    public void desequipar() {
        itemEquipado = null;
    }

    public Item getItemEquipado() { return itemEquipado; }

    public List<Item> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public List<Item> getItensPorTipo(String tipo) {
        return itens.stream()
                .filter(i -> i.getTipo().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());
    }

    public int contarFragmentos() {
        return (int) itens.stream()
                .filter(Item::isFragmento)
                .count();
    }

    public boolean temItem(String nome) {
        return itens.stream().anyMatch(i -> i.getNome().equalsIgnoreCase(nome));
    }

    public void limpar() {
        itens.clear();
        itemEquipado = null;
    }

    public int tamanho() { return itens.size(); }
}