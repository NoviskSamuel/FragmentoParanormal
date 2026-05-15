package com.mycompany.fragmentoparanormal.enums;

public enum Classe {
    ESPECIALISTA("Especialista", "+Investigação, mais chance de achar itens"),
    COMBATENTE("Combatente", "+Dano e resistência física"),
    OCULTISTA("Ocultista", "+Dano à longa distância, resistência a criaturas");

    private final String nome;
    private final String descricao;

    Classe(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
}