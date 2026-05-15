package com.mycompany.fragmentoparanormal.model;


import com.mycompany.fragmentoparanormal.enums.Classe;
import com.mycompany.fragmentoparanormal.enums.Elemento;
import com.mycompany.fragmentoparanormal.enums.Genero;

public class Personagem {
    private int id;
    private String nome;
    private int nivel;
    private Classe classe;
    private Genero genero;
    private Elemento elemento;
    private String imagemPath;
    private int xpAtual;
    private int xpProximoNivel;

    // Atributos de combate
    private int forca;
    private int poderParanormal;
    private int investigacao;
    private int vidaMaxima;
    private int vidaAtual;
    private int moedas;

    private Inventario inventario = new Inventario();

    public Personagem() {}

    public Personagem(String nome, Classe classe, Genero genero, Elemento elemento) {
        this.nome = nome;
        this.classe = classe;
        this.genero = genero;
        this.elemento = elemento;
        this.nivel = 1;
        this.xpAtual = 0;
        this.xpProximoNivel = 100;
        this.moedas = 0;
        aplicarBonusClasse();
    }

    private void aplicarBonusClasse() {
        // Base
        this.forca = 10;
        this.poderParanormal = 10;
        this.investigacao = 10;
        this.vidaMaxima = 100;
        this.vidaAtual = 100;

        switch (classe) {
            case ESPECIALISTA:
                this.investigacao += 5;
                this.poderParanormal += 2;
                break;
            case COMBATENTE:
                this.forca += 5;
                this.vidaMaxima += 20;
                this.vidaAtual += 20;
                break;
            case OCULTISTA:
                this.poderParanormal += 5;
                this.forca += 2;
                break;
        }

        // Definir imagem padrão por classe/gênero
        this.imagemPath = resolverImagem();
    }

    private String resolverImagem() {
        if (classe == null || genero == null) return "/images/personagens/default.png";
        return switch (classe) {
            case ESPECIALISTA -> genero == Genero.MASCULINO
                    ? "/images/personagens/arthur.png"
                    : "/images/personagens/erin.png";
            case COMBATENTE -> genero == Genero.MASCULINO
                    ? "/images/personagens/dominic.png"
                    : "/images/personagens/carina.png";
            case OCULTISTA -> genero == Genero.MASCULINO
                    ? "/images/personagens/dante.png"
                    : "/images/personagens/agatha.png";
        };
    }

    public boolean estaMorto() { return vidaAtual <= 0; }

    public void receberDano(int dano) {
        this.vidaAtual = Math.max(0, this.vidaAtual - dano);
    }

    public void curar(int quantidade) {
        this.vidaAtual = Math.min(vidaMaxima, this.vidaAtual + quantidade);
    }

    public void ganharXP(int xp) {
        this.xpAtual += xp;
        while (this.xpAtual >= this.xpProximoNivel) {
            this.xpAtual -= this.xpProximoNivel;
            this.nivel++;
            this.xpProximoNivel = (int)(this.xpProximoNivel * 1.5);
        }
    }

    public void subirAtributo(String atributo, int pontos) {
        switch (atributo.toLowerCase()) {
            case "forca"            -> this.forca += pontos;
            case "poder"            -> this.poderParanormal += pontos;
            case "investigacao"     -> this.investigacao += pontos;
            case "vida"             -> { this.vidaMaxima += pontos * 5; this.vidaAtual += pontos * 5; }
        }
    }
    
    public int getId()
    { return id; }
    public void setId(int id)
    { this.id = id; }
    public String getNome()
    { return nome; }
    public void setNome(String nome)
    { this.nome = nome; }
    public int getNivel()
    { return nivel; }
    public void setNivel(int nivel)
    { this.nivel = nivel; }
    public Classe getClasse() 
    { return classe; }
    public void setClasse(Classe classe) { this.classe = classe; }
    public Genero getGenero() 
    { return genero; }
    public void setGenero(Genero genero) { this.genero = genero; }
    public Elemento getElemento()
    { return elemento; }
    public void setElemento(Elemento elemento)
    { this.elemento = elemento; }
    public String getImagemPath()
    { return imagemPath; }
    public void setImagemPath(String imagemPath)
    { this.imagemPath = imagemPath; }
    public int getXpAtual() 
    { return xpAtual; }
    public void setXpAtual(int xpAtual) 
    { this.xpAtual = xpAtual; }
    public int getXpProximoNivel(
    ) { return xpProximoNivel; }
    public void setXpProximoNivel(int v) 
    { this.xpProximoNivel = v; }
    public int getForca() 
    { return forca; }
    public void setForca(int forca)
    { this.forca = forca; }
    public int getPoderParanormal()
    { return poderParanormal; }
    public void setPoderParanormal(int p) 
    { this.poderParanormal = p; }
    public int getInvestigacao() 
    { return investigacao; }
    public void setInvestigacao(int investigacao)
    { this.investigacao = investigacao; }
    public int getVidaMaxima() 
    { return vidaMaxima; }
    public void setVidaMaxima(int vidaMaxima)
    { this.vidaMaxima = vidaMaxima; }
    public int getVidaAtual()
    { return vidaAtual; }
    public void setVidaAtual(int vidaAtual)
    { this.vidaAtual = vidaAtual; }
    public int getMoedas()
    { return moedas; }
    public void setMoedas(int moedas)
    { this.moedas = moedas; }
    public Inventario getInventario()
    { return inventario; }
    public void setInventario(Inventario inventario)
    { this.inventario = inventario; }
}
}