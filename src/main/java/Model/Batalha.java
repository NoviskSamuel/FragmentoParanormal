package Model;

import Util.AcaoBatalha;
import java.util.Random;

public class Batalha {
    private Personagem jogador;
    private Inimigo inimigo;
    private boolean emAndamento;
    private boolean fugiu;
    private String ultimoLog;
    private static final Random rand = new Random();

    public Batalha(Personagem jogador, Inimigo inimigo) {
        this.jogador     = jogador;
        this.inimigo     = inimigo;
        this.emAndamento = true;
        this.fugiu       = false;
    }

    public String executarAcao(AcaoBatalha acao) {
        if (!emAndamento) return "Batalha já encerrada.";

        StringBuilder log = new StringBuilder();

        switch (acao) {
            case ATACAR -> {
                int dano = calcularDanoFisico();
                inimigo.receberDano(dano);
                log.append("Você atacou ").append(inimigo.getNome())
                   .append(" causando ").append(dano).append(" de dano!\n");
            }
            case USAR_RITUAL -> {
                int dano = calcularDanoParanormal();
                inimigo.receberDano(dano);
                log.append("Ritual executado! ").append(inimigo.getNome())
                   .append(" sofreu ").append(dano).append(" de dano paranormal!\n");
            }
            case EQUIPAR_ARMA -> {
                Item arma = jogador.getInventario().getArmaEquipada();
                if (arma != null) {
                    int dano = Math.max(1, jogador.getForca() + arma.getValor() + rand.nextInt(4));
                    inimigo.receberDano(dano);
                    log.append("Atacou com ").append(arma.getNome())
                       .append(" causando ").append(dano).append(" de dano!\n");
                } else {
                    int dano = calcularDanoFisico();
                    inimigo.receberDano(dano);
                    log.append("Nenhuma arma equipada. Ataque com os punhos!\n");
                    log.append("Causou ").append(dano).append(" de dano.\n");
                }
            }
            case FUGIR -> {
                boolean fugaOk = rand.nextInt(100) < 50;
                if (fugaOk) {
                    emAndamento = false;
                    this.fugiu  = true;
                    this.ultimoLog = "Você fugiu com sucesso!";
                    return ultimoLog;
                } else {
                    log.append("Tentativa de fuga falhou!\n");
                }
            }
        }

        if (inimigo.estaMorto()) {
            emAndamento = false;
            log.append(inimigo.getNome()).append(" foi derrotado!\n");
            log.append("+ ").append(inimigo.getXpConcedido()).append(" XP\n");
            log.append("+ ").append(inimigo.getMoedasDrop()).append(" moedas");
            this.ultimoLog = log.toString();
            return ultimoLog;
        }

        int danoInimigo = Math.max(1, inimigo.getForca() - rand.nextInt(4));
        jogador.receberDano(danoInimigo);
        log.append(inimigo.getNome()).append(" contra-atacou causando ")
           .append(danoInimigo).append(" de dano!\n");
        log.append("Sua vida: ").append(jogador.getVidaAtual())
           .append("/").append(jogador.getVidaMaxima());

        if (jogador.estaMorto()) {
            emAndamento = false;
            log.append("\nVocê foi derrotado...");
        }

        this.ultimoLog = log.toString();
        return ultimoLog;
    }

    private int calcularDanoFisico()     { return Math.max(1, jogador.getForca()           + rand.nextInt(6)); }
    private int calcularDanoParanormal() { return Math.max(1, jogador.getPoderParanormal() + rand.nextInt(8)); }

    public Item rolarDrop(java.util.List<Item> pool) {
        if (pool == null || pool.isEmpty()) return null;
        int chance = Math.min(55, 25 + jogador.getInvestigacao() / 2);
        return rand.nextInt(100) < chance ? pool.get(rand.nextInt(pool.size())) : null;
    }

    public boolean isEmAndamento() { return emAndamento; }
    public boolean jogadorVenceu() { return !emAndamento && inimigo.estaMorto(); }
    public boolean jogadorMorreu() { return !emAndamento && jogador.estaMorto(); }
    public boolean jogadorFugiu()  { return !emAndamento && fugiu; }
    public String  getUltimoLog()  { return ultimoLog; }
    public Personagem getJogador() { return jogador; }
    public Inimigo    getInimigo() { return inimigo; }
}