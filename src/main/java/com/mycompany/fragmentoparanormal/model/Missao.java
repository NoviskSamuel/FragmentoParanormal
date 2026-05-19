package com.mycompany.fragmentoparanormal.model;

/**
 * Classe modelo para representar uma Missão no jogo
 */
public class Missao {
    private int id;
    private String titulo;
    private String descricao;
    private String objetivo;
    private int totalObjetivo;
    private int nivelMinimo;
    private int totalSalas;
    private int progressoAtual;
    private int salaAtual;
    private boolean concluida;
    private int vezesRetornou;
    private boolean fugiu;

    public Missao() {
    }

    public Missao(int id, String titulo, String descricao, String objetivo,
                  int totalObjetivo, int nivelMinimo, int totalSalas) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.objetivo = objetivo;
        this.totalObjetivo = totalObjetivo;
        this.nivelMinimo = nivelMinimo;
        this.totalSalas = totalSalas;
        this.progressoAtual = 0;
        this.salaAtual = 0;
        this.concluida = false;
        this.vezesRetornou = 0;
        this.fugiu = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    public int getTotalObjetivo() { return totalObjetivo; }
    public void setTotalObjetivo(int totalObjetivo) { this.totalObjetivo = totalObjetivo; }

    public int getNivelMinimo() { return nivelMinimo; }
    public void setNivelMinimo(int nivelMinimo) { this.nivelMinimo = nivelMinimo; }

    public int getTotalSalas() { return totalSalas; }
    public void setTotalSalas(int totalSalas) { this.totalSalas = totalSalas; }

    public int getProgressoAtual() { return progressoAtual; }
    public void setProgressoAtual(int progressoAtual) { this.progressoAtual = progressoAtual; }

    public int getSalaAtual() { return salaAtual; }
    public void setSalaAtual(int salaAtual) { this.salaAtual = salaAtual; }

    public boolean isConcluida() { return concluida; }
    public void setConcluida(boolean concluida) { this.concluida = concluida; }

    public int getVezesRetornou() { return vezesRetornou; }
    public void setVezesRetornou(int vezesRetornou) { this.vezesRetornou = vezesRetornou; }

    public boolean isFugiu() { return fugiu; }
    public void setFugiu(boolean fugiu) { this.fugiu = fugiu; }

    @Override
    public String toString() {
        return "Missao{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", concluida=" + concluida +
                '}';
    }
}
