package Model;

import Util.Elemento;

public class Inimigo {

    private int     id;
    private String  nome;
    private Elemento elemento;
    private int     nivel;
    private String  imagemPath;
    private String  imagemJumpscare;
    private int     xpConcedido;
    private int     forca;
    private int     vidaMaxima;
    private int     vidaAtual;
    private int     moedasDrop;
    private String  descricao;

    public Inimigo() {}

    public Inimigo(String nome, Elemento elemento, int nivel, int forca, int vida, int xp) {
        this.nome       = nome;
        this.elemento   = elemento;
        this.nivel      = nivel;
        this.forca      = forca;
        this.vidaMaxima = vida;
        this.vidaAtual  = vida;
        this.xpConcedido = xp;
        this.moedasDrop  = Math.max(1, xp / 2);
    }

    public boolean estaMorto() { return vidaAtual <= 0; }

    public void receberDano(int dano) {
        this.vidaAtual = Math.max(0, this.vidaAtual - Math.max(0, dano));
    }

    public void escalar(double fator) {
        if (fator <= 0) return;
        this.forca       = Math.max(1, (int) (this.forca * fator));
        this.vidaMaxima  = Math.max(1, (int) (this.vidaMaxima * fator));
        this.vidaAtual   = this.vidaMaxima;
        this.xpConcedido = Math.max(1, (int) (this.xpConcedido * fator));
        this.moedasDrop  = Math.max(1, this.xpConcedido / 2);
    }

    public int     getId()                          { return id; }
    public void    setId(int id)                    { this.id = id; }
    public String  getNome()                        { return nome; }
    public void    setNome(String nome)             { this.nome = nome; }
    public Elemento getElemento()                   { return elemento; }
    public void    setElemento(Elemento e)          { this.elemento = e; }
    public int     getNivel()                       { return nivel; }
    public void    setNivel(int nivel)              { this.nivel = nivel; }
    public String  getImagemPath()                  { return imagemPath; }
    public void    setImagemPath(String p)          { this.imagemPath = p; }
    public String  getImagemJumpscare()             { return imagemJumpscare; }
    public void    setImagemJumpscare(String img)   { this.imagemJumpscare = img; }
    public int     getXpConcedido()                 { return xpConcedido; }
    public void    setXpConcedido(int xp)           { this.xpConcedido = xp; }
    public int     getForca()                       { return forca; }
    public void    setForca(int forca)              { this.forca = Math.max(1, forca); }
    public int     getVidaMaxima()                  { return vidaMaxima; }
    public void    setVidaMaxima(int v)             { this.vidaMaxima = Math.max(1, v); }
    public int     getVidaAtual()                   { return vidaAtual; }
    public void    setVidaAtual(int v)              { this.vidaAtual = Math.max(0, Math.min(v, vidaMaxima)); }
    public int     getMoedasDrop()                  { return moedasDrop; }
    public void    setMoedasDrop(int m)             { this.moedasDrop = Math.max(0, m); }
    public String  getDescricao()                   { return descricao; }
    public void    setDescricao(String d)           { this.descricao = d; }
}