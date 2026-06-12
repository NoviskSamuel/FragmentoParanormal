package Model;

public class Ranking {

    private int    posicao;
    private String nomeJogador;
    private int    nivel;
    private String missaoAtual;
    private int    moedasTotais;
    private int    inimigosAbatidos;

    public Ranking() {}

    public Ranking(String nomeJogador, int nivel, String missaoAtual,
                   int moedasTotais, int inimigosAbatidos) {
        this.nomeJogador      = nomeJogador;
        this.nivel            = nivel;
        this.missaoAtual      = missaoAtual;
        this.moedasTotais     = moedasTotais;
        this.inimigosAbatidos = inimigosAbatidos;
    }

    public int    getPosicao()               { return posicao; }
    public void   setPosicao(int posicao)    { this.posicao = posicao; }
    public String getNomeJogador()           { return nomeJogador; }
    public void   setNomeJogador(String n)   { this.nomeJogador = n; }
    public int    getNivel()                 { return nivel; }
    public void   setNivel(int nivel)        { this.nivel = nivel; }
    public String getMissaoAtual()           { return missaoAtual; }
    public void   setMissaoAtual(String m)   { this.missaoAtual = m; }
    public int    getMoedasTotais()          { return moedasTotais; }
    public void   setMoedasTotais(int m)     { this.moedasTotais = m; }
    public int    getInimigosAbatidos()      { return inimigosAbatidos; }
    public void   setInimigosAbatidos(int i) { this.inimigosAbatidos = i; }
}