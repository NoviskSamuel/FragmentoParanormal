package Util;

public enum Genero {
    MASCULINO("Masculino"),
    FEMININO("Feminino");

    private final String nome;

    Genero(String nome) { this.nome = nome; }

    public String getNome() { return nome; }
}