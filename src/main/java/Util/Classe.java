package Util;

public enum Classe {

    ESPECIALISTA("Especialista",
            "+Investigação — maior chance de achar itens e fragmentos",
            5, 0, 2, 0),

    COMBATENTE("Combatente",
            "+Força e resistência física — mais vida e dano corpo a corpo",
            0, 5, 0, 20),

    OCULTISTA("Ocultista",
            "+Poder Paranormal — dano elevado com rituais, resistência a criaturas",
            0, 2, 5, 0);

    private final String nome;
    private final String descricao;
    private final int bonusInvestigacao;
    private final int bonusForca;
    private final int bonusPoder;
    private final int bonusVida;

    Classe(String nome, String descricao,
           int bonusInvestigacao, int bonusForca, int bonusPoder, int bonusVida) {
        this.nome              = nome;
        this.descricao         = descricao;
        this.bonusInvestigacao = bonusInvestigacao;
        this.bonusForca        = bonusForca;
        this.bonusPoder        = bonusPoder;
        this.bonusVida         = bonusVida;
    }

    public String getNome()           { return nome; }
    public String getDescricao()      { return descricao; }
    public int getBonusInvestigacao() { return bonusInvestigacao; }
    public int getBonusForca()        { return bonusForca; }
    public int getBonusPoder()        { return bonusPoder; }
    public int getBonusVida()         { return bonusVida; }
}