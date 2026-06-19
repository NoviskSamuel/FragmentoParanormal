package Controller;

import Dao.MissaoDAO;
import Dao.PersonagemDAO;
import Dao.RankingDAO;
import Graphics.SpriteManager;
import Model.Batalha;
import Model.Inimigo;
import Model.Item;
import Model.Missao;
import Model.Personagem;
import Util.AcaoBatalha;
import Util.Elemento;
import Util.ScreenManager;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class MissaoController {

    @FXML private Label     labelTitulo;
    @FXML private Label     labelObjetivo;
    @FXML private Label     labelSala;
    @FXML private Label     labelVida;
    @FXML private Label     labelVidaInimigo;
    @FXML private Label     labelNarrative;
    @FXML private Label     labelFragmento;
    @FXML private Label     labelStamina;

    @FXML private ImageView imgPersonagem;
    @FXML private ImageView imgInimigo;
    @FXML private ImageView imgJumpscare;
    @FXML private ImageView imgCenarioFundo;

    @FXML private HBox painelAcoesMissao;
    @FXML private HBox painelBatalha;

    @FXML private VBox  painelItem;
    @FXML private Label labelItemEncontrado;
    @FXML private Label labelItemDesc;

    // Botão voltar — referenciado para habilitar/desabilitar
    @FXML private Button btnVoltar;

    private Personagem jogador;
    private Missao     missao;
    private Batalha    batalhaAtual;
    private int        inimigosMortos = 0;
    private int        moedasSessao   = 0;

    private boolean emCooldown = false;

    private static final long   COOLDOWN_MS = 2000;
    private static final Random RAND = new Random();

    @FXML
    public void initialize() {
        jogador = ScreenManager.getInstance().getPersonagemAtivo();
        missao  = ScreenManager.getInstance().getMissaoAtiva();

        SpriteManager.getInstance().setSprite(imgPersonagem, jogador.getImagemPath());
        SpriteManager.getInstance().animarIdle(imgPersonagem);

        imgJumpscare.setVisible(false);
        imgJumpscare.setOpacity(0);
        imgInimigo.setVisible(false);

        setBatalhaVisivel(false);
        painelItem.setVisible(false);
        painelItem.setManaged(false);

        if (ScreenManager.getInstance().temEstadoMissaoSalvo()) {
            restaurarEstadoMissao();
        } else {
            atualizarHUD();
            labelNarrative.setText("Você chegou ao local. Avance com cuidado, Agente " + jogador.getNome() + ".");
        }

        atualizarBotaoVoltar();
        atualizarCenario();
    }

    private void restaurarEstadoMissao() {
        ScreenManager sm = ScreenManager.getInstance();

        batalhaAtual   = sm.getBatalhaSalva();
        inimigosMortos = sm.getInimigosMortosSalvos();
        moedasSessao   = sm.getMoedasSessaoSalvas();
        String narrativa = sm.getNarrativaSalva();
        sm.limparEstadoMissao();

        atualizarHUD();

        if (batalhaAtual != null && batalhaAtual.isEmAndamento()) {
            Inimigo in = batalhaAtual.getInimigo();
            SpriteManager.getInstance().setSprite(imgInimigo, in.getImagemPath());
            imgInimigo.setVisible(true);
            imgInimigo.setOpacity(1.0);
            atualizarVidaInimigo();
            setBatalhaVisivel(true);
        } else {
            setBatalhaVisivel(false);
        }

        labelNarrative.setText(narrativa != null && !narrativa.isBlank()
            ? narrativa
            : "Você retornou à missão, Agente " + jogador.getNome() + ". Continue avançando.");

        atualizarCenario();
    }

    private void atualizarHUD() {
        jogador.regenerarStamina();
        labelTitulo.setText(missao.getTitulo());
        labelObjetivo.setText("Objetivo: " + missao.getObjetivo() + " — " + missao.getProgressoTexto());

        // Mostra nome do local atual se disponível
        Missao.LocalMapa local = missao.getLocalAtualObj();
        if (local != null) {
            labelSala.setText("📍 " + local.getNome()
                    + (local.isPaginaEncontrada() ? " ✓" : "")
                    + "  [" + (missao.getLocalAtual() + 1) + "/" + missao.getLocais().size() + "]");
        } else {
            labelSala.setText("Sala " + missao.getSalaAtual() + " / " + missao.getTotalSalas());
        }

        labelVida.setText("❤ " + jogador.getVidaAtual() + " / " + jogador.getVidaMaxima());
        labelFragmento.setText("📜 Fragmentos: " + missao.getProgressoTexto());
        labelStamina.setText("⚡ " + jogador.getStaminaAtual() + " / " + jogador.getStaminaMaxima());
    }

    private void atualizarInfoLocal() {
        Missao.LocalMapa local = missao.getLocalAtualObj();
        if (local == null) return;
        atualizarHUD();
        if (!local.isPaginaEncontrada() && !local.isBossRoom()) {
            labelNarrative.setText("📍 " + local.getNome()
                    + "\n" + local.getDescricao()
                    + "\n\n🔍 Investigue este local para encontrar a página do diário.");
        }
    }

    private void atualizarBotaoVoltar() {
        if (btnVoltar != null) {
            btnVoltar.setDisable(missao.primeiraSala());
        }
    }

    private boolean verificarCooldown() {
        if (emCooldown) {
            labelNarrative.setText("⏳ Aguarde um momento antes de agir novamente...");
            return false;
        }
        return true;
    }

    private void iniciarCooldown() {
        emCooldown = true;
        painelAcoesMissao.setDisable(true);
        painelBatalha.setDisable(true);
        PauseTransition cd = new PauseTransition(Duration.millis(COOLDOWN_MS));
        cd.setOnFinished(e -> {
            emCooldown = false;
            painelAcoesMissao.setDisable(false);
            painelBatalha.setDisable(false);
        });
        cd.play();
    }

    private boolean exigirStamina(int custo) {
        jogador.regenerarStamina();
        if (jogador.getStaminaAtual() < custo) {
            labelNarrative.setText("⚡ Stamina insuficiente! Descanse antes de continuar.");
            atualizarHUD();
            emCooldown = true;
            painelAcoesMissao.setDisable(true);
            painelBatalha.setDisable(true);
            PauseTransition pausa = new PauseTransition(Duration.seconds(1.2));
            pausa.setOnFinished(e -> {
                emCooldown = false;
                painelAcoesMissao.setDisable(false);
                painelBatalha.setDisable(false);
                onDescansar();
            });
            pausa.play();
            return false;
        }
        jogador.consumirStamina(custo);
        atualizarHUD();
        return true;
    }

    // ── AVANÇAR ───────────────────────────────────────────────────
    @FXML
    private void onAvancar() {
        if (!verificarCooldown()) return;
        if (missao.ultimaSala()) {
            labelNarrative.setText("Você chegou ao fim da área. Nenhum fragmento restante foi encontrado.");
            return;
        }
        if (!exigirStamina(Personagem.CUSTO_STAMINA_AVANCAR)) return;
        iniciarCooldown();
        missao.avancarSala();
        atualizarHUD();
        atualizarBotaoVoltar();
        atualizarCenario();

        // Sala já visitada — não gera novo evento
        if (missao.salaTemInimigoDerrotado(missao.getSalaAtual())) {
            labelNarrative.setText("Você voltou a esta sala. Não há mais nada aqui além de silêncio.");
        } else {
            gerarEvento();
        }
    }

    // ── VOLTAR ────────────────────────────────────────────────────
    @FXML
    private void onVoltarSala() {
        if (!verificarCooldown()) return;
        if (missao.primeiraSala()) {
            labelNarrative.setText("Você já está na primeira sala.");
            return;
        }
        if (!exigirStamina(Personagem.CUSTO_STAMINA_AVANCAR)) return;
        iniciarCooldown();
        missao.voltarSala();
        atualizarHUD();
        atualizarBotaoVoltar();
        atualizarCenario();

        boolean inimigoMorto  = missao.salaTemInimigoDerrotado(missao.getSalaAtual());
        boolean jaInvestigada = missao.salaJaFoiInvestigada(missao.getSalaAtual());

        if (inimigoMorto && jaInvestigada) {
            labelNarrative.setText("Você voltou a esta sala. Tudo já foi vasculhado — não há mais nada aqui.");
        } else if (inimigoMorto) {
            labelNarrative.setText("Você voltou a esta sala. O inimigo foi derrotado, mas ainda é possível investigar.");
        } else {
            labelNarrative.setText("Você recuou para a sala anterior.");
        }
    }
    @FXML
    private void onInvestigar() {
        if (!verificarCooldown()) return;

        if (missao.salaJaFoiInvestigada(missao.getSalaAtual())) {
            labelNarrative.setText("🔍 Você já vasculhou esta sala completamente.");
            return;
        }

        if (!exigirStamina(Personagem.CUSTO_STAMINA_INVESTIGAR)) return;
        iniciarCooldown();
        // Marca DEPOIS de investigar — só bloqueia se realmente encontrou algo
        realizarInvestigacaoComMarca();
    }

    private void realizarInvestigacaoComMarca() {
        // Chance base de achar fragmento: 40-65% dependendo de investigação
        int chanceFragmento = Math.min(65, 40 + jogador.getInvestigacao() / 2);
        int roll = RAND.nextInt(100);

        if (roll < chanceFragmento) {
            // Achou a página — marca o local e bloqueia investigação
            missao.marcarPaginaEncontrada();
            missao.marcarSalaInvestigada(missao.getSalaAtual());

            Item pagina = new Item("Fragmento do Diário", "FRAGMENTO", 0,
                    "Página " + (missao.getProgressoAtual()) + " — " + getNomePaginaAtual());
            jogador.getInventario().adicionarItem(pagina);

            atualizarHUD();
            atualizarInfoLocal();
            labelItemEncontrado.setText("📜 Página encontrada!");
            labelItemDesc.setText(pagina.getDescricao()
                    + "\n\n" + missao.getProgressoTexto() + " páginas coletadas.");
            painelItem.setVisible(true);
            painelItem.setManaged(true);
            verificarConclusao();

        } else if (roll < chanceFragmento + 25) {
            // Achou item secundário (poção/arma) — pode continuar investigando
            Item item = gerarItemSecundario();
            jogador.getInventario().adicionarItem(item);
            labelItemEncontrado.setText("🔍 Encontrou: " + item.getNome());
            labelItemDesc.setText(item.getDescricao());
            painelItem.setVisible(true);
            painelItem.setManaged(true);
            if (item.isPocao()) {
                jogador.curar(item.getValor());
                atualizarHUD();
                labelItemDesc.setText(item.getDescricao() + "\n✅ Vida restaurada! +" + item.getValor());
            }
        } else {
            // Não achou nada — pode tentar de novo
            labelNarrative.setText("Você vasculhou cuidadosamente, mas não encontrou a página. Tente novamente.");
        }
    }

    private String getNomePaginaAtual() {
        Missao.LocalMapa local = missao.getLocalAtualObj();
        return local != null ? local.getNome() : "Local Desconhecido";
    }

    private Item gerarItemSecundario() {
        int r = RAND.nextInt(100);
        if (r < 50) return new Item("Poção de Ervas",   "POCAO", 30, "Restaura 30 pontos de vida.");
        if (r < 75) return new Item("Faca Enferrujada", "ARMA",   8, "Uma faca velha mas ainda cortante.");
        return          new Item("Amuleto Sombrio",   "RITUAL",  12, "Amplifica rituais paranormais.");
    }

    private void realizarInvestigacao() {
        int chance = Math.min(85, 30 + jogador.getInvestigacao() * 2);
        if (RAND.nextInt(100) < chance) {
            Item item = gerarItem();
            jogador.getInventario().adicionarItem(item);
            labelItemEncontrado.setText("🔍 Encontrou: " + item.getNome());
            labelItemDesc.setText(item.getDescricao());
            painelItem.setVisible(true);
            painelItem.setManaged(true);

            if (item.isFragmento()) {
                missao.incrementarProgresso();
                atualizarHUD();
                verificarConclusao();
            }
            if (item.isPocao()) {
                jogador.curar(item.getValor());
                atualizarHUD();
                labelItemDesc.setText(item.getDescricao() + "\n✅ Vida restaurada! +" + item.getValor());
            }
        } else {
            labelNarrative.setText("Algo chamou sua atenção, mas não havia nada de útil.");
        }
    }

    @FXML
    private void onFecharItem() {
        painelItem.setVisible(false);
        painelItem.setManaged(false);
    }

    // ── FUGIR DA MISSÃO ───────────────────────────────────────────
    @FXML
    private void onFugirMissao() {
        if (!verificarCooldown()) return;
        missao.setFugiu(true);
        missao.setVezesRetornou(missao.getVezesRetornou() + 1);
        salvarProgresso();
        salvarPersonagem();
        ScreenManager.getInstance().limparEstadoMissao();
        try {
            new RankingDAO().registrarPartida(
                jogador.getId(), missao.getId(), "FUGA", inimigosMortos, moedasSessao);
        } catch (SQLException ignored) {}
        labelNarrative.setText("🏃 Você fugiu. Progresso salvo. Ao retornar, os inimigos estarão mais fortes.");
        emCooldown = true;
        painelAcoesMissao.setDisable(true);
        PauseTransition pausa = new PauseTransition(Duration.seconds(2));
        pausa.setOnFinished(e -> ScreenManager.getInstance().irSemHistorico(ScreenManager.TELA_MISSOES));
        pausa.play();
    }

    @FXML private void onInventario() {
    if (!verificarCooldown()) return;
    ScreenManager.getInstance().salvarEstadoMissao(batalhaAtual, inimigosMortos, moedasSessao, labelNarrative.getText());
    ScreenManager.getInstance().abrirInventarioComoJanela(); // <- era .ir(TELA_INVENTARIO)
}

    @FXML
    private void onDescansar() {
        salvarPersonagem();
        ScreenManager.getInstance().salvarEstadoMissao(batalhaAtual, inimigosMortos, moedasSessao, labelNarrative.getText());
        ScreenManager.getInstance().ir(ScreenManager.TELA_DESCANSO);
    }

    // ── BATALHA ───────────────────────────────────────────────────
    @FXML private void onAtacar()      { executarBatalha(AcaoBatalha.ATACAR); }
    @FXML private void onRitual()      { executarBatalha(AcaoBatalha.USAR_RITUAL); }
    @FXML private void onEquiparArma() { executarBatalha(AcaoBatalha.EQUIPAR_ARMA); }
    @FXML private void onFugirBatalha(){ executarBatalha(AcaoBatalha.FUGIR); }

    private void executarBatalha(AcaoBatalha acao) {
        if (!verificarCooldown()) return;
        if (batalhaAtual == null || !batalhaAtual.isEmAndamento()) return;

        int custoStamina = switch (acao) {
    case ATACAR, EQUIPAR_ARMA -> Personagem.CUSTO_STAMINA_ATACAR;
    case USAR_RITUAL          -> Personagem.CUSTO_STAMINA_RITUAL;
    case FUGIR                -> Personagem.CUSTO_STAMINA_FUGIR;
};
    if (!exigirStamina(custoStamina)) return;
        iniciarCooldown();

        SpriteManager.getInstance().animarAtaque(imgPersonagem, null);

        String log = batalhaAtual.executarAcao(acao);
        labelNarrative.setText(log);

        if (batalhaAtual.getInimigo().getVidaAtual() < batalhaAtual.getInimigo().getVidaMaxima()) {
            SpriteManager.getInstance().animarDano(imgInimigo);
        }
        if (jogador.getVidaAtual() < jogador.getVidaMaxima()) {
            SpriteManager.getInstance().animarDano(imgPersonagem);
        }

        atualizarHUD();
        atualizarVidaInimigo();

        if (!batalhaAtual.isEmAndamento()) {
            processarFimDeBatalha();
        }
    }

    private void processarFimDeBatalha() {
        if (batalhaAtual.jogadorVenceu()) {
            // Marca a sala como "inimigo derrotado"
            missao.marcarInimigoDerrotado(missao.getSalaAtual());

            inimigosMortos++;
            moedasSessao += batalhaAtual.getInimigo().getMoedasDrop();

            int xpBase       = batalhaAtual.getInimigo().getXpConcedido();
            int nivelInimigo = batalhaAtual.getInimigo().getNivel();
            boolean subiuNivel = jogador.ganharXP(xpBase, nivelInimigo);

            List<Item> pool = List.of(
                new Item("Faca Enferrujada",    "ARMA",      8,  "Uma faca velha mas ainda cortante."),
                new Item("Fragmento do Diário", "FRAGMENTO", 0,  "Uma página rasgada do diário perdido."),
                new Item("Poção de Ervas",       "POCAO",    25, "Restaura 25 pontos de vida."),
                new Item("Amuleto Sombrio",      "RITUAL",   12, "Amplifica poder paranormal em rituais.")
            );
            Item drop = batalhaAtual.rolarDrop(pool);
            if (drop != null) {
                jogador.getInventario().adicionarItem(drop);
                labelItemEncontrado.setText("🎁 Drop: " + drop.getNome());
                labelItemDesc.setText(drop.getDescricao());
                painelItem.setVisible(true);
                painelItem.setManaged(true);
                if (drop.isFragmento()) missao.incrementarProgresso();
                if (drop.isPocao())     jogador.curar(drop.getValor());
            }

            SpriteManager.getInstance().animarMorte(imgInimigo, () -> {
                imgInimigo.setVisible(false);
                setBatalhaVisivel(false);
                atualizarHUD();
                verificarConclusao();
                if (!missao.objetivoConcluido()) {
                    String xpMsg = nivelInimigo < jogador.getNivel() - 3
                        ? " (XP reduzido — inimigo muito fraco)" : "";
                    labelNarrative.setText("Inimigo derrotado! Continue avançando." + xpMsg);
                }
                if (subiuNivel && !missao.objetivoConcluido()) {
                    PauseTransition p = new PauseTransition(Duration.seconds(1));
                    p.setOnFinished(e -> ScreenManager.getInstance().ir(ScreenManager.TELA_LEVEL_UP));
                    p.play();
                }
            });
            salvarProgresso();

        } else if (batalhaAtual.jogadorMorreu()) {
    SpriteManager.getInstance().animarMorte(imgPersonagem, () -> {
        missao.setProgressoAtual(0);
        missao.setSalaAtual(0);
        missao.setFugiu(false);
        missao.setVezesRetornou(0);
        jogador.restaurarVidaTotal();
        jogador.restaurarStaminaTotal();
        salvarProgresso();
        salvarPersonagem();
                ScreenManager.getInstance().limparEstadoMissao();
                try {
                    new RankingDAO().registrarPartida(
                        jogador.getId(), missao.getId(), "MORTE", inimigosMortos, moedasSessao);
                } catch (SQLException ignored) {}
                PauseTransition p = new PauseTransition(Duration.seconds(2));
                p.setOnFinished(e -> ScreenManager.getInstance().irSemHistorico(ScreenManager.TELA_MISSOES));
                p.play();
            });

       } else if (batalhaAtual.jogadorFugiu()) {
    imgInimigo.setVisible(false);
    setBatalhaVisivel(false);
    jogador.recuperarStamina(Personagem.CUSTO_STAMINA_FUGIR / 2);
    atualizarHUD();
    labelNarrative.setText("Você fugiu da batalha!");
}
    }

    private void gerarEvento() {
        int r = RAND.nextInt(100);
        if (r < 55) {
            Inimigo inimigo = gerarInimigo();
            if (missao.isFugiu() && missao.getVezesRetornou() > 0) {
                inimigo.escalar(1.0 + 0.25 * missao.getVezesRetornou());
            }
            String jumpPath = inimigo.getImagemJumpscare() != null
                ? inimigo.getImagemJumpscare() : inimigo.getImagemPath();
            SpriteManager.getInstance().exibirJumpscare(imgJumpscare, jumpPath,
                () -> iniciarBatalha(inimigo));
        } else if (r < 75) {
            labelNarrative.setText("A sala está vazia. Apenas ecos e sombras...");
        } else {
            labelNarrative.setText("Algo chama sua atenção nesta sala...");
            realizarInvestigacao();
        }
    }

    private void iniciarBatalha(Inimigo inimigo) {
        batalhaAtual = new Batalha(jogador, inimigo);
        SpriteManager.getInstance().setSprite(imgInimigo, inimigo.getImagemPath());
        imgInimigo.setVisible(true);
        imgInimigo.setOpacity(1.0);
        atualizarVidaInimigo();
        setBatalhaVisivel(true);
        labelNarrative.setText("⚔ " + inimigo.getNome() + " apareceu! O que você vai fazer?");
    }

    private void atualizarVidaInimigo() {
        if (batalhaAtual != null) {
            Inimigo in = batalhaAtual.getInimigo();
            labelVidaInimigo.setText("👾 " + in.getNome() +
                "  ❤ " + in.getVidaAtual() + "/" + in.getVidaMaxima());
        }
    }

    private void setBatalhaVisivel(boolean visivel) {
        painelBatalha.setVisible(visivel);
        painelBatalha.setManaged(visivel);
        painelAcoesMissao.setVisible(!visivel);
        painelAcoesMissao.setManaged(!visivel);
        labelVidaInimigo.setVisible(visivel);
    }

    private Inimigo gerarInimigo() {
        record Cfg(String nome, String img) {}
        List<Cfg> tipos = List.of(
            new Cfg("Enraizado",       "enraizado.png"),
            new Cfg("Zumbi de Sangue", "zumbisangue.png"),
            new Cfg("Existido",        "existido.png"),
            new Cfg("Anárquico",       "anarquico.png")
        );
        Cfg c   = tipos.get(RAND.nextInt(tipos.size()));
        int sal = Math.max(1, missao.getSalaAtual());
        Elemento[] elementos = {
            Elemento.CONHECIMENTO, Elemento.ENERGIA,
            Elemento.MORTE,        Elemento.SANGUE
        };
        Elemento elementoAleatorio = elementos[RAND.nextInt(elementos.length)];
        int nivelInimigo = missao.getNivelMinimo() + (sal - 1);
        Inimigo in = new Inimigo(c.nome(), elementoAleatorio,
            nivelInimigo, 6 + sal * 2, 25 + sal * 8, 15 + sal * 5);
        in.setImagemPath(c.img());
        in.setImagemJumpscare(c.img().replace(".png", "_jump.png"));
        return in;
    }

    private Item gerarItem() {
        int r = RAND.nextInt(100);
        if      (r < 4) return new Item("Fragmento do Diário", "FRAGMENTO", 0,  "Uma página rasgada do diário perdido.");
        else if (r < 70) return new Item("Poção de Ervas",       "POCAO",    30, "Restaura 30 pontos de vida.");
        else if (r < 85) return new Item("Faca Enferrujada",     "ARMA",      8, "Uma faca velha mas ainda cortante.");
        else              return new Item("Amuleto Sombrio",      "RITUAL",   12, "Amplifica rituais paranormais.");
    }

    private void verificarConclusao() {
        if (missao.objetivoConcluido()) {
            if (missao.bossLiberado() && !missao.isConcluida()) {
                labelNarrative.setText("🏆 Todas as páginas encontradas! O boss foi liberado — avance para o confronto final!");
            }
            missao.setConcluida(true);
            salvarProgresso();
            ScreenManager.getInstance().limparEstadoMissao();
            int xpRecompensa = 300 + missao.getNivelMinimo() * 100;
            boolean subiuNivel = jogador.ganharXP(xpRecompensa, jogador.getNivel());
            jogador.setMoedas(jogador.getMoedas() + moedasSessao);
            salvarPersonagem();
            try {
                new RankingDAO().registrarPartida(
                    jogador.getId(), missao.getId(), "VITORIA", inimigosMortos, moedasSessao);
            } catch (SQLException ignored) {}

            labelNarrative.setText("🏆 MISSÃO CONCLUÍDA!\nTodos os fragmentos foram coletados!");
            emCooldown = true;
            painelAcoesMissao.setDisable(true);
            painelBatalha.setDisable(true);
            PauseTransition p = new PauseTransition(Duration.seconds(2));
            if (subiuNivel) {
                p.setOnFinished(e -> ScreenManager.getInstance().ir(ScreenManager.TELA_LEVEL_UP));
            } else {
                p.setOnFinished(e -> ScreenManager.getInstance().irSemHistorico(ScreenManager.TELA_MISSOES));
            }
            p.play();
        }
    }

    private void atualizarCenario() {
        if (imgCenarioFundo == null || missao == null) return;
        try {
            int salaAtual  = Math.max(1, missao.getSalaAtual());
            int totalSalas = missao.getTotalSalas();
            int idMissao   = missao.getId();

            int cenarioId = idMissao;
            if (idMissao == 4)      cenarioId = 2;
            else if (idMissao >= 5) cenarioId = 1;

            String nomeImagem;
            if (salaAtual >= totalSalas) {
                nomeImagem = "Missao" + cenarioId + "_salaFinal.jpg";
            } else {
                nomeImagem = "Missao" + cenarioId + "_sala" + Math.min(salaAtual, 4) + ".jpg";
            }

            var resource = getClass().getResource("/fragmentoparanormal/cenarios/" + nomeImagem);
            if (resource != null) {
                imgCenarioFundo.setImage(new javafx.scene.image.Image(resource.toExternalForm()));
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar cenário: " + e.getMessage());
        }
    }

    private void salvarProgresso() {
        if (jogador == null || missao == null) return;
        try { new MissaoDAO().salvarProgresso(jogador.getId(), missao); }
        catch (SQLException e) { System.err.println("Erro ao salvar progresso: " + e.getMessage()); }
    }

    private void salvarPersonagem() {
        try { new PersonagemDAO().atualizar(jogador); }
        catch (SQLException e) { System.err.println("Erro ao salvar personagem: " + e.getMessage()); }
    }
    
    
}
