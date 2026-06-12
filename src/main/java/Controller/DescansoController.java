package Controller;

import Dao.PersonagemDAO;
import Graphics.SpriteManager;
import Model.Personagem;
import Util.ScreenManager;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Turno de Descanso — minigame de Sequência de Reflexos.
 *
 * Regras:
 *  • O jogo exibe uma sequência de 4 símbolos (🜁🜂🜃🜄) piscando um a um.
 *  • O jogador deve clicar os botões na mesma ordem.
 *  • Acerto total → recupera STAMINA_RECOMPENSA pontos de stamina.
 *  • Erro →  tenta de novo (máximo MAX_TENTATIVAS vezes); na última falha,
 *    recupera apenas STAMINA_CONSOLACAO pontos e volta à missão.
 *  • "Pular" → sai sem recuperar nada.
 *
 * Integração:
 *  • MissaoController chama ScreenManager.getInstance().ir(ScreenManager.TELA_DESCANSO)
 *    quando stamina == 0 ou o jogador clica em "Descansar".
 *  • Ao terminar, este controller volta para TELA_MISSAO (sem limpar histórico).
 */
public class DescansoController {

    // ── FXML ──────────────────────────────────────────────────────────────────
    @FXML private Label     labelNomePersonagem;
    @FXML private Label     labelStaminaAtual;
    @FXML private Label     labelInstrucao;
    @FXML private Label     labelFeedback;
    @FXML private Label     labelTentativas;

    @FXML private ImageView imgPersonagem;

    // Botões de input do jogador
    @FXML private Button btnSimbolo0;   // 🜁 Fogo
    @FXML private Button btnSimbolo1;   // 🜂 Terra
    @FXML private Button btnSimbolo2;   // 🜃 Água
    @FXML private Button btnSimbolo3;   // 🜄 Ar

    @FXML private Button btnPular;

    // ── Constantes ────────────────────────────────────────────────────────────
    private static final int TAMANHO_SEQUENCIA  = 4;
    private static final int MAX_TENTATIVAS     = 3;
    private static final int STAMINA_RECOMPENSA = 40;   // acerto completo
    private static final int STAMINA_CONSOLACAO = 10;   // esgotou tentativas

    private static final String[] SIMBOLOS = { "🜁", "🜂", "🜃", "🜄" };
    // Nomes amigáveis exibidos durante a animação
    private static final String[] NOMES_SIMBOLOS = { "Fogo", "Terra", "Água", "Ar" };

    // Estilos de destaque para cada símbolo durante a animação
    private static final String[] ESTILOS_DESTAQUE = {
        "btn-descanso-fogo",
        "btn-descanso-terra",
        "btn-descanso-agua",
        "btn-descanso-ar"
    };

    // ── Estado ────────────────────────────────────────────────────────────────
    private Personagem    jogador;
    private List<Integer> sequencia      = new ArrayList<>();
    private List<Integer> inputJogador   = new ArrayList<>();
    private int           tentativasRestantes = MAX_TENTATIVAS;
    private boolean       exibindoSequencia   = false;

    private static final Random RAND = new Random();

    // ── Inicialização ─────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        jogador = ScreenManager.getInstance().getPersonagemAtivo();

        SpriteManager.getInstance().setSprite(imgPersonagem, jogador.getImagemPath());
        SpriteManager.getInstance().animarIdle(imgPersonagem);

        labelNomePersonagem.setText(jogador.getNome());
        atualizarHUD();

        setBotoesSimbolosAtivos(false);
        labelInstrucao.setText("Respire fundo, Agente. Observe a sequência...");
        labelFeedback.setText("");

        // Pequena pausa dramática antes de começar
        PauseTransition pausa = new PauseTransition(Duration.seconds(1.2));
        pausa.setOnFinished(e -> iniciarRodada());
        pausa.play();
    }

    // ── Lógica principal ──────────────────────────────────────────────────────

    private void iniciarRodada() {
        sequencia.clear();
        inputJogador.clear();
        gerarSequencia();
        labelFeedback.setText("");
        labelTentativas.setText("Tentativas restantes: " + tentativasRestantes);
        labelInstrucao.setText("Memorize a sequência:");
        setBotoesSimbolosAtivos(false);
        animarSequencia();
    }

    private void gerarSequencia() {
        for (int i = 0; i < TAMANHO_SEQUENCIA; i++) {
            sequencia.add(RAND.nextInt(SIMBOLOS.length));
        }
    }

    /**
     * Pisca os botões um a um na ordem da sequência gerada.
     * Ao terminar, libera os botões para o input do jogador.
     */
    private void animarSequencia() {
        exibindoSequencia = true;
        Button[] botoes = { btnSimbolo0, btnSimbolo1, btnSimbolo2, btnSimbolo3 };

        Timeline tl = new Timeline();
        double intervalo = 0.7; // segundos entre cada flash

        for (int i = 0; i < sequencia.size(); i++) {
            final int idx = sequencia.get(i);
            final double tempoInicioFlash = i * intervalo * 2;
            final double tempoFimFlash    = tempoInicioFlash + intervalo;

            // Liga o destaque
            KeyFrame kfOn = new KeyFrame(Duration.seconds(tempoInicioFlash), e -> {
                botoes[idx].getStyleClass().add(ESTILOS_DESTAQUE[idx]);
                labelInstrucao.setText("▶  " + NOMES_SIMBOLOS[idx]);
            });

            // Desliga o destaque
            KeyFrame kfOff = new KeyFrame(Duration.seconds(tempoFimFlash), e -> {
                botoes[idx].getStyleClass().removeAll(
                    "btn-descanso-fogo", "btn-descanso-terra",
                    "btn-descanso-agua", "btn-descanso-ar"
                );
                labelInstrucao.setText("...");
            });

            tl.getKeyFrames().addAll(kfOn, kfOff);
        }

        // Ao terminar a animação, libera input
        double tempoTotal = sequencia.size() * intervalo * 2;
        KeyFrame kfFim = new KeyFrame(Duration.seconds(tempoTotal), e -> {
            exibindoSequencia = false;
            labelInstrucao.setText("Agora! Repita a sequência:");
            setBotoesSimbolosAtivos(true);
        });
        tl.getKeyFrames().add(kfFim);
        tl.play();
    }

    // ── Handlers dos botões de símbolo ────────────────────────────────────────

    @FXML private void onSimbolo0() { registrarInput(0); }
    @FXML private void onSimbolo1() { registrarInput(1); }
    @FXML private void onSimbolo2() { registrarInput(2); }
    @FXML private void onSimbolo3() { registrarInput(3); }

    private void registrarInput(int simbolo) {
        if (exibindoSequencia) return;

        inputJogador.add(simbolo);
        int pos = inputJogador.size() - 1;

        // Checa se o símbolo está certo nesta posição
        if (simbolo != sequencia.get(pos)) {
            // ERRO
            tentativasRestantes--;
            setBotoesSimbolosAtivos(false);

            if (tentativasRestantes <= 0) {
                // Esgotou tentativas → consolação
                labelFeedback.setText("❌ Sequência errada! Você está exausto demais...");
                labelInstrucao.setText("Descansando um pouco mesmo assim.");
                PauseTransition p = new PauseTransition(Duration.seconds(2));
                p.setOnFinished(e -> finalizarDescanso(STAMINA_CONSOLACAO, false));
                p.play();
            } else {
                // Ainda tem tentativas → mostra sequência de novo
                labelFeedback.setText("❌ Errou! Mais " + tentativasRestantes
                    + " tentativa" + (tentativasRestantes == 1 ? "" : "s") + ".");
                labelTentativas.setText("Tentativas restantes: " + tentativasRestantes);
                PauseTransition p = new PauseTransition(Duration.seconds(1.5));
                p.setOnFinished(e -> {
                    inputJogador.clear();
                    labelFeedback.setText("");
                    labelInstrucao.setText("Observe novamente:");
                    animarSequencia();
                });
                p.play();
            }
            return;
        }

        // Feedback visual positivo no label
        labelFeedback.setText("✔ " + NOMES_SIMBOLOS[simbolo]);

        // Checa se completou a sequência
        if (inputJogador.size() == sequencia.size()) {
            setBotoesSimbolosAtivos(false);
            labelFeedback.setText("✅ Sequência correta!");
            labelInstrucao.setText("Você se concentrou e recuperou as forças!");
            PauseTransition p = new PauseTransition(Duration.seconds(1.8));
            p.setOnFinished(e -> finalizarDescanso(STAMINA_RECOMPENSA, true));
            p.play();
        }
    }

    @FXML
    private void onPular() {
        if (exibindoSequencia) return;
        labelFeedback.setText("Você pulou o descanso.");
        setBotoesSimbolosAtivos(false);
        btnPular.setDisable(true);
        PauseTransition p = new PauseTransition(Duration.seconds(1));
        p.setOnFinished(e -> finalizarDescanso(0, false));
        p.play();
    }

    // ── Finalização ───────────────────────────────────────────────────────────

    /**
     * @param staminaRecuperada quantos pontos de stamina devolver ao jogador.
     * @param sucesso           true = acerto; false = falha/skip (apenas para log futuro).
     */
    private void finalizarDescanso(int staminaRecuperada, boolean sucesso) {
        if (staminaRecuperada > 0) {
            jogador.recuperarStamina(staminaRecuperada);
            labelInstrucao.setText("⚡ +" + staminaRecuperada + " Stamina recuperada!");
            atualizarHUD();
        }
        salvarPersonagem();

        PauseTransition espera = new PauseTransition(Duration.seconds(1.5));
        espera.setOnFinished(e -> ScreenManager.getInstance().voltar());
        espera.play();
    }

    // ── Utilitários ───────────────────────────────────────────────────────────

    private void atualizarHUD() {
        labelStaminaAtual.setText(
            "⚡ " + jogador.getStaminaAtual() + " / " + jogador.getStaminaMaxima()
        );
    }

    private void setBotoesSimbolosAtivos(boolean ativo) {
        btnSimbolo0.setDisable(!ativo);
        btnSimbolo1.setDisable(!ativo);
        btnSimbolo2.setDisable(!ativo);
        btnSimbolo3.setDisable(!ativo);
    }

    private void salvarPersonagem() {
        try {
            new PersonagemDAO().atualizar(jogador);
        } catch (SQLException e) {
            System.err.println("Erro ao salvar stamina: " + e.getMessage());
        }
    }
}