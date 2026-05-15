package com.mycompany.fragmentoparanormal.model;

import com.mycompany.fragmentoparanormal.enums.Elemento;

public class Inimigo {
    private int id;
    private String nome;
    private Elemento elemento;
    private int nivel;
    private String imagemPath;
    private String imagemJumpscare;
    private int xpConcedido;
    private int forca;
    private int vidaMaxima;
    private int vidaAtual;
    private int moedasDrop;
    private String descricao;

    public Inimigo() {}

    public Inimigo(String nome, Elemento elemento, int nivel, int forca, int vida, int xp) {
        this.nome = nome;
        this.elemento = elemento;
        this.nivel = nivel;
        this.forca = forca;
        this.vidaMaxima = vida;
        this.vidaAtual = vida;
        this.xpConcedido = xp;
        this.moedasDrop = xp / 2;
    }

    public boolean estaMorto() { return vidaAtual <= 0; }

    public void receberDano(int dano) {
        this.vidaAtual = Math.max(0, this.vidaAtual - dano);
    }

    /** Escala o inimigo quando jogador retorna após fuga */
    public void escalar(double fator) {
        this.forca = (int)(this.forca * fator);
        this.vidaMaxima = (int)(this.vidaMaxima * fator);
        this.vidaAtual = this.vidaMaxima;
        this.xpConcedido = (int)(this.xpConcedido * fator);
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Elemento getElemento() { return elemento; }
    public void setElemento(Elemento elemento) { this.elemento = elemento; }
    public int getNivel() { return nivel; }
    public void setNivel(int nivel) { this.nivel = nivel; }
    public String getImagemPath() { return imagemPath; }
    public void setImagemPath(String imagemPath) { this.imagemPath = imagemPath; }
    public String getImagemJumpscare() { return imagemJumpscare; }
    public void setImagemJumpscare(String img) { this.imagemJumpscare = img; }
    public int getXpConcedido() { return xpConcedido; }
    public void setXpConcedido(int xpConcedido) { this.xpConcedido = xpConcedido; }
    public int getForca() { return forca; }
    public void setForca(int forca) { this.forca = forca; }
    public int getVidaMaxima() { return vidaMaxima; }
    public void setVidaMaxima(int v) { this.vidaMaxima = v; }
    public int getVidaAtual() { return vidaAtual; }
    public void setVidaAtual(int vidaAtual) { this.vidaAtual = vidaAtual; }
    public int getMoedasDrop() { return moedasDrop; }
    public void setMoedasDrop(int m) { this.moedasDrop = m; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}