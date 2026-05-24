package Model;

import Util.Classe;
import Util.Elemento;
import Util.Genero;

public class Personagem {

    private int      id;
    private String   nome;
    private int      nivel;
    private Classe   classe;
    private Genero   genero;
    private Elemento elemento;
    private String   imagemPath;
    private int      xpAtual;
    private int      xpProximoNivel;

    private int forca;
    private int poderParanormal;
    private int investigacao;
    private int vidaMaxima;
    private int vidaAtual;
    private int moedas;

    private Inventario inventario = new Inventario();

    public Personagem() {}

    public Personagem(String nome, Classe classe, Genero genero, Elemento elemento) {
        this.nome     = nome;
        this.classe   = classe;
        this.genero   = genero;
        this.elemento = elemento;
        this.nivel    = 1;
        this.xpAtual  = 0;
        this.xpProximoNivel = 100;
        this.moedas   = 0;
        aplicarBonusClasse();
    }

    private void aplicarBonusClasse() {
        this.forca           = 10;
        this.poderParanormal = 10;
        this.investigacao    = 10;
        this.vidaMaxima      = 100;

        this.forca           += classe.getBonusForca();
        this.poderParanormal += classe.getBonusPoder();
        this.investigacao    += classe.getBonusInvestigacao();
        this.vidaMaxima      += classe.getBonusVida();
        this.vidaAtual        = this.vidaMaxima;

        this.imagemPath = resolverImagem();
    }

    public String resolverImagem() {
        if (classe == null || genero == null)
            return "/com/mycompany/fragmentoparanormal/images/personagens/default.png";
        return switch (classe) {
            case ESPECIALISTA -> genero == Genero.MASCULINO
                    ? "/com/mycompany/fragmentoparanormal/images/personagens/arthur.png"
                    : "/com/mycompany/fragmentoparanormal/images/personagens/erin.png";
            case COMBATENTE   -> genero == Genero.MASCULINO
                    ? "/com/mycompany/fragmentoparanormal/images/personagens/dominic.png"
                    : "/com/mycompany/fragmentoparanormal/images/personagens/carina.png";
            case OCULTISTA    -> genero == Genero.MASCULINO
                    ? "/com/mycompany/fragmentoparanormal/images/personagens/dante.png"
                    : "/com/mycompany/fragmentoparanormal/images/personagens/agatha.png";
        };
    }

    public boolean estaMorto() { return vidaAtual <= 0; }

    public void receberDano(int dano) {
        this.vidaAtual = Math.max(0, this.vidaAtual - Math.max(0, dano));
    }

    public void curar(int quantidade) {
        this.vidaAtual = Math.min(vidaMaxima, this.vidaAtual + Math.max(0, quantidade));
    }

    public boolean ganharXP(int xp) {
        if (xp <= 0) return false;
        this.xpAtual += xp;
        boolean subiu = false;
        while (this.xpProximoNivel > 0 && this.xpAtual >= this.xpProximoNivel) {
            this.xpAtual -= this.xpProximoNivel;
            this.nivel++;
            this.xpProximoNivel = (int) (this.xpProximoNivel * 1.5);
            subiu = true;
        }
        return subiu;
    }

    public void subirAtributo(String atributo, int pontos) {
        if (pontos <= 0) return;
        String a = atributo.toLowerCase().trim();
        if      (a.equals("forca"))        this.forca           += pontos;
        else if (a.equals("poder"))        this.poderParanormal += pontos;
        else if (a.equals("investigacao")) this.investigacao     += pontos;
        else if (a.equals("vida")) {
            int bonus = pontos * 5;
            this.vidaMaxima += bonus;
            this.vidaAtual  = Math.min(vidaAtual + bonus, vidaMaxima);
        }
    }

    public void restaurarVidaTotal() {
        this.vidaAtual = this.vidaMaxima;
    }

    public int    getId()                         { return id; }
    public void   setId(int id)                   { this.id = id; }
    public String getNome()                       { return nome; }
    public void   setNome(String nome)            { this.nome = nome; }
    public int    getNivel()                      { return nivel; }
    public void   setNivel(int nivel)             { this.nivel = nivel; }
    public Classe getClasse()                     { return classe; }
    public void   setClasse(Classe classe)        { this.classe = classe; }
    public Genero getGenero()                     { return genero; }
    public void   setGenero(Genero genero)        { this.genero = genero; }
    public Elemento getElemento()                 { return elemento; }
    public void     setElemento(Elemento e)       { this.elemento = e; }
    public String getImagemPath()                 { return imagemPath; }
    public void   setImagemPath(String p)         { this.imagemPath = p; }
    public int  getXpAtual()                      { return xpAtual; }
    public void setXpAtual(int xpAtual)           { this.xpAtual = xpAtual; }
    public int  getXpProximoNivel()               { return xpProximoNivel; }
    public void setXpProximoNivel(int v)          { this.xpProximoNivel = v; }
    public int  getForca()                        { return forca; }
    public void setForca(int forca)               { this.forca = forca; }
    public int  getPoderParanormal()              { return poderParanormal; }
    public void setPoderParanormal(int p)         { this.poderParanormal = p; }
    public int  getInvestigacao()                 { return investigacao; }
    public void setInvestigacao(int investigacao) { this.investigacao = investigacao; }
    public int  getVidaMaxima()                   { return vidaMaxima; }
    public void setVidaMaxima(int vidaMaxima)     { this.vidaMaxima = vidaMaxima; }
    public int  getVidaAtual()                    { return vidaAtual; }
    public void setVidaAtual(int vidaAtual)       { this.vidaAtual = Math.max(0, Math.min(vidaAtual, vidaMaxima)); }
    public int  getMoedas()                       { return moedas; }
    public void setMoedas(int moedas)             { this.moedas = Math.max(0, moedas); }
    public Inventario getInventario()             { return inventario; }
    public void       setInventario(Inventario i) { this.inventario = i; }
}