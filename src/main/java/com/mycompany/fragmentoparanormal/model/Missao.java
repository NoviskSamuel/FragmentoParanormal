package com.mycompany.fragmentoparanormal.model;

import java.util.ArrayList;
import java.util.List;

public class Missao {
    private int id;
    private String titulo;
    private String descricao;
    private String objetivo;        // ex: "Coletar 7 fragmentos do diário perdido"
    private int totalObjetivo;      // ex: 7
    private int progressoAtual;     // incrementado durante a missão
    private int nivelMinimo;        // nível mínimo para desbloquear
    private boolean concluida;
    private boolean desbloqueada;
    private List<Inimigo> inimigos = new ArrayList<>();
    private List<Item> itensDisponiveis = new ArrayList<>();
    private int salaAtual;
    private int totalSalas;
    private boolean fugiu;
    private int vezesRetornou;      // quantas vezes o jogador fugiu e voltou

    public Missao() {}

    public Missao(String titulo, String descricao, String objetivo,
                  int totalObjetivo, int nivelMinimo, int totalSalas) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.objetivo = objetivo;
        this.totalObjetivo = totalObjetivo;
        this.nivelMinimo = nivelMinimo;
        this.totalSalas = totalSalas;
        this.salaAtual = 0;
        this.progressoAtual = 0;
        this.desbloqueada = nivelMinimo <= 1;
    }

    public boolean isCompleta() {
        return progressoAtual >= totalObjetivo;
    }

    public void avancarProgresso() {
        if (progressoAtual < totalObjetivo) progressoAtual++;
        if (isCompleta()) concluida = true;
    }

    public void reiniciar() {
        this.salaAtual = 0;
        this.progressoAtual = 0;
        this.concluida = false;
        this.fugiu = false;
        this.vezesRetornou = 0;
    }

    public void registrarFuga() {
        this.fugiu = true;
        this.vezesRetornou++;
    }

    public double getFatorEscalamento() {
        return 1.0 + (vezesRetornou * 0.25);
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public int getTotalObjetivo() { return totalObjetivo; }
    public void setTotalObjetivo(int t) { this.totalObjetivo = t; }
    public int getProgressoAtual() { return progressoAtual; }
    public void setProgressoAtual(int p) { this.progressoAtual = p; }
    public int getNivelMinimo() { return nivelMinimo; }
    public void setNivelMinimo(int n) { this.nivelMinimo = n; }
    public boolean isConcluida() { return concluida; }
    public void setConcluida(boolean concluida) { this.concluida = concluida; }
    public boolean isDesbloqueada() { return desbloqueada; }
    public void setDesbloqueada(boolean d) { this.desbloqueada = d; }
    public List<Inimigo> getInimigos() { return inimigos; }
    public void setInimigos(List<Inimigo> inimigos) { this.inimigos = inimigos; }
    public List<Item> getItensDisponiveis() { return itensDisponiveis; }
    public void setItensDisponiveis(List<Item> itens) { this.itensDisponiveis = itens; }
    public int getSalaAtual() { return salaAtual; }
    public void setSalaAtual(int salaAtual) { this.salaAtual = salaAtual; }
    public int getTotalSalas() { return totalSalas; }
    public void setTotalSalas(int totalSalas) { this.totalSalas = totalSalas; }
    public boolean isFugiu() { return fugiu; }
    public void setFugiu(boolean fugiu) { this.fugiu = fugiu; }
    public int getVezesRetornou() { return vezesRetornou; }
    public void setVezesRetornou(int v) { this.vezesRetornou = v; }
}
