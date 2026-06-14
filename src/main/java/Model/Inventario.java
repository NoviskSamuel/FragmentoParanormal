package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Inventario {

    private final List<Item> itens = new ArrayList<>();
    private Item itemEquipado;

    // Métodos para o Controller buscar listas específicas por Categoria/Tipo
    public List<Item> getArmas() {
        return getItensPorTipo("Arma");
    }

    public List<Item> getHabilidades() {
        return getItensPorTipo("Habilidade");
    }

    public List<Item> getRituais() {
        return getItensPorTipo("Ritual");
    }

    public List<Item> getConsumiveis() {
        return getItensPorTipo("Consumivel");
    }

    public List<Item> getArtefatos() {
        return getItensPorTipo("Artefato");
    }

    // Métodos para o Controller verificar os Equipados específicos
    public Item getArmaEquipada() {
        return (itemEquipado != null && itemEquipado.getTipo().equalsIgnoreCase("Arma")) ? itemEquipado : null;
    }

    public Item getRitualEquipado() {
        return (itemEquipado != null && itemEquipado.getTipo().equalsIgnoreCase("Ritual")) ? itemEquipado : null;
    }

    public Item getHabilidadeEquipada() {
        return (itemEquipado != null && itemEquipado.getTipo().equalsIgnoreCase("Habilidade")) ? itemEquipado : null;
    }

    // O Controller espera uma lista de artefatos equipados, adaptamos usando o item atual se for artefato
    public List<Item> getArtefatosEquipados() {
        List<Item> equipados = new ArrayList<>();
        if (itemEquipado != null && itemEquipado.getTipo().equalsIgnoreCase("Artefato")) {
            equipados.add(itemEquipado);
        }
        return equipados;
    }

    // ─────────────────────────────────────────────────────────────
    // Seus métodos originais mantidos intactos abaixo:
    // ─────────────────────────────────────────────────────────────

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
                .filter(i -> i.getTipo() != null && i.getTipo().equalsIgnoreCase(tipo))
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