package Model;

import Util.AcaoBatalha;
import java.util.Random;

public class Batalha {
    private Personagem jogador;
    private Inimigo inimigo;
    private boolean emAndamento;
    private String ultimoLog;
    private static final Random rand = new Random();

    public Batalha(Personagem jogador, Inimigo inimigo) {
        this.jogador = jogador;
        this.inimigo = inimigo;
        this.emAndamento = true;
    }

    public String executarAcao(AcaoBatalha acao) {
        if (!emAndamento) return "Batalha já encerrada.";

        StringBuilder log = new StringBuilder();

        switch (acao) {
            case ATACAR -> {
                int dano = calcularDanoJogador(false);
                inimigo.receberDano(dano);
                log.append("Você atacou ").append(inimigo.getNome())
                   .append(" causando ").append(dano).append(" de dano!\n");
            }
            case USAR_RITUAL -> {
                int dano = calcularDanoJogador(true);
                inimigo.receberDano(dano);
                log.append("Ritual executado! ").append(inimigo.getNome())
                   .append(" sofreu ").append(dano).append(" de dano paranormal!\n");
            }
            case EQUIPAR_ARMA -> {
                Item arma = jogador.getInventario().getItemEquipado();
                if (arma != null && arma.getTipo().equals("ARMA")) {
                    int dano = jogador.getForca() + arma.getValor();
                    inimigo.receberDano(dano);
                    log.append("Atacou com ").append(arma.getNome())
                       .append(" causando ").append(dano).append(" de dano!\n");
                } else {
                    log.append("Nenhuma arma equipada. Ataque com os punhos!\n");
                    int dano = calcularDanoJogador(false);
                    inimigo.receberDano(dano);
                    log.append("Causou ").append(dano).append(" de dano.\n");
                }
            }
            case FUGIR -> {
                boolean fugiu = rand.nextInt(100) < 50;
                if (fugiu) {
                    emAndamento = false;
                    this.ultimoLog = log.append("Você fugiu com sucesso!").toString();
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

        // Contra-ataque do inimigo
        int danoInimigo = Math.max(1, inimigo.getForca() - rand.nextInt(5));
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

    private int calcularDanoJogador(boolean paranormal) {
        int base = paranormal ? jogador.getPoderParanormal() : jogador.getForca();
        return base + rand.nextInt(6);
    }

    /** Rola chance de drop após vitória */
    public Item rolarDrop(java.util.List<Item> pool) {
        if (pool == null || pool.isEmpty()) return null;
        int chance = 40 + jogador.getInvestigacao();
        if (rand.nextInt(100) < chance) {
            return pool.get(rand.nextInt(pool.size()));
        }
        return null;
    }

    public boolean isEmAndamento() { return emAndamento; }
    public boolean jogadorVenceu() { return inimigo.estaMorto(); }
    public boolean jogadorMorreu() { return jogador.estaMorto(); }
    public String getUltimoLog() { return ultimoLog; }
    public Personagem getJogador() { return jogador; }
    public Inimigo getInimigo() { return inimigo; }
}
