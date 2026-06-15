package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Inventario {

    private final List<Item> itens = new ArrayList<>();

    private Item armaEquipada;
    private Item ritualEquipado;
    private Item habilidadeEquipada;
    private final List<Item> artefatosEquipados = new ArrayList<>();

    private static final int MAX_ARTEFATOS = 2;

    public List<Item> getArmas()       { return getItensPorTipo("ARMA"); }
    public List<Item> getHabilidades() { return getItensPorTipo("HABILIDADE"); }
    public List<Item> getRituais()     { return getItensPorTipo("RITUAL"); }
    public List<Item> getConsumiveis() { return getItensPorTipo("POCAO"); }
    public List<Item> getArtefatos()   { return getItensPorTipo("ARTEFATO"); }

    public Item       getArmaEquipada()      { return armaEquipada; }
    public Item       getRitualEquipado()    { return ritualEquipado; }
    public Item       getHabilidadeEquipada(){ return habilidadeEquipada; }
    public List<Item> getArtefatosEquipados(){ return Collections.unmodifiableList(artefatosEquipados); }

    public Item getItemEquipado() {
        if (armaEquipada      != null) return armaEquipada;
        if (ritualEquipado    != null) return ritualEquipado;
        if (habilidadeEquipada != null) return habilidadeEquipada;
        return null;
    }

    public boolean equiparItem(Item item) {
        if (item == null || !itens.contains(item)) return false;

        String tipo = item.getTipo() == null ? "" : item.getTipo().toUpperCase();
        switch (tipo) {
            case "ARMA"       -> armaEquipada       = item;
            case "RITUAL"     -> ritualEquipado      = item;
            case "HABILIDADE" -> habilidadeEquipada  = item;
            case "ARTEFATO"   -> {
                if (!artefatosEquipados.contains(item)) {
                    if (artefatosEquipados.size() >= MAX_ARTEFATOS) {
                        artefatosEquipados.remove(0);
                    }
                    artefatosEquipados.add(item);
                }
            }
            default -> { return false; }
        }
        return true;
    }

    public boolean estaEquipado(Item item) {
        if (item == null) return false;
        return item.equals(armaEquipada)
            || item.equals(ritualEquipado)
            || item.equals(habilidadeEquipada)
            || artefatosEquipados.contains(item);
    }

    public void desequipar() {
        armaEquipada       = null;
        ritualEquipado     = null;
        habilidadeEquipada = null;
        artefatosEquipados.clear();
    }

    public void adicionarItem(Item item) {
        if (item != null) itens.add(item);
    }

    public void removerItem(Item item) {
        itens.remove(item);
        if (item == null) return;
        if (item.equals(armaEquipada))       armaEquipada       = null;
        if (item.equals(ritualEquipado))     ritualEquipado     = null;
        if (item.equals(habilidadeEquipada)) habilidadeEquipada = null;
        artefatosEquipados.remove(item);
    }

    public List<Item> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public List<Item> getItensPorTipo(String tipo) {
        return itens.stream()
                .filter(i -> i.getTipo() != null && i.getTipo().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());
    }

    public int contarFragmentos() {
        return (int) itens.stream().filter(Item::isFragmento).count();
    }

    public boolean temItem(String nome) {
        return itens.stream().anyMatch(i -> i.getNome().equalsIgnoreCase(nome));
    }

    public void limpar() {
        itens.clear();
        desequipar();
    }

    public int tamanho() { return itens.size(); }
}