package Model;

import java.util.HashMap;
import java.util.Map;

public class Missao {

    private int     id;
    private String  titulo;
    private String  descricao;
    private String  objetivo;
    private int     totalObjetivo;
    private int     nivelMinimo;
    private int     totalSalas;
    private int     progressoAtual;
    private int     salaAtual;
    private boolean concluida;
    private int     vezesRetornou;
    private boolean fugiu;

    // Rastreia o estado de cada sala: inimigo derrotado e investigação realizada
    private final Map<Integer, Boolean> salasComInimigoDerrotado  = new HashMap<>();
    private final Map<Integer, Boolean> salasJaInvestigadas       = new HashMap<>();

    public Missao() {}

    public Missao(int id, String titulo, String descricao, String objetivo,
                  int totalObjetivo, int nivelMinimo, int totalSalas) {
        this.id             = id;
        this.titulo         = titulo;
        this.descricao      = descricao;
        this.objetivo       = objetivo;
        this.totalObjetivo  = totalObjetivo;
        this.nivelMinimo    = nivelMinimo;
        this.totalSalas     = totalSalas;
        this.progressoAtual = 0;
        this.salaAtual      = 0;
        this.concluida      = false;
        this.vezesRetornou  = 0;
        this.fugiu          = false;
    }

    public boolean objetivoConcluido() {
        return progressoAtual >= totalObjetivo;
    }

    public boolean ultimaSala() {
        return salaAtual >= totalSalas;
    }

    public boolean primeiraSala() {
        return salaAtual <= 0;
    }

    public int avancarSala() {
        salaAtual = Math.min(salaAtual + 1, totalSalas);
        return salaAtual;
    }

    public int voltarSala() {
        salaAtual = Math.max(0, salaAtual - 1);
        return salaAtual;
    }

    public int incrementarProgresso() {
        progressoAtual = Math.min(progressoAtual + 1, totalObjetivo);
        return progressoAtual;
    }

    public String getProgressoTexto() {
        return progressoAtual + "/" + totalObjetivo;
    }

    // ── Estado por sala ───────────────────────────────────────────
    public void marcarInimigoDerrotado(int sala) {
        salasComInimigoDerrotado.put(sala, true);
    }

    public boolean salaTemInimigoDerrotado(int sala) {
        return salasComInimigoDerrotado.getOrDefault(sala, false);
    }

    public void marcarSalaInvestigada(int sala) {
        salasJaInvestigadas.put(sala, true);
    }

    public boolean salaJaFoiInvestigada(int sala) {
        return salasJaInvestigadas.getOrDefault(sala, false);
    }

    // ── Getters / Setters ─────────────────────────────────────────
    public int     getId()                          { return id; }
    public void    setId(int id)                    { this.id = id; }
    public String  getTitulo()                      { return titulo; }
    public void    setTitulo(String titulo)         { this.titulo = titulo; }
    public String  getDescricao()                   { return descricao; }
    public void    setDescricao(String d)           { this.descricao = d; }
    public String  getObjetivo()                    { return objetivo; }
    public void    setObjetivo(String o)            { this.objetivo = o; }
    public int     getTotalObjetivo()               { return totalObjetivo; }
    public void    setTotalObjetivo(int t)          { this.totalObjetivo = t; }
    public int     getNivelMinimo()                 { return nivelMinimo; }
    public void    setNivelMinimo(int n)            { this.nivelMinimo = n; }
    public int     getTotalSalas()                  { return totalSalas; }
    public void    setTotalSalas(int t)             { this.totalSalas = t; }
    public int     getProgressoAtual()              { return progressoAtual; }
    public void    setProgressoAtual(int p)         { this.progressoAtual = Math.max(0, p); }
    public int     getSalaAtual()                   { return salaAtual; }
    public void    setSalaAtual(int s)              { this.salaAtual = Math.max(0, s); }
    public boolean isConcluida()                    { return concluida; }
    public void    setConcluida(boolean c)          { this.concluida = c; }
    public int     getVezesRetornou()               { return vezesRetornou; }
    public void    setVezesRetornou(int v)          { this.vezesRetornou = Math.max(0, v); }
    public boolean isFugiu()                        { return fugiu; }
    public void    setFugiu(boolean f)              { this.fugiu = f; }
}