package com.mycompany.fragmentoparanormal.model;

public class Item {
    private int id;
    private String nome;
    private String tipo;       // ARMA, POCAO, RITUAL, FRAGMENTO
    private int valor;         // dano se arma, cura se poção
    private String imagemPath;
    private String descricao;

    public Item() {}

    public Item(String nome, String tipo, int valor, String descricao) {
        this.nome = nome;
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = descricao;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public int getValor() { return valor; }
    public void setValor(int valor) { this.valor = valor; }
    public String getImagemPath() { return imagemPath; }
    public void setImagemPath(String imagemPath) { this.imagemPath = imagemPath; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    @Override
    public String toString() { return nome + " [" + tipo + "] +" + valor; }
}
