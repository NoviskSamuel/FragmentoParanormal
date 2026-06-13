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

public class DescansoController {

    @FXML private Label     labelNomePersonagem;
    @FXML private Label     labelStaminaAtual;
    @FXML private Label     labelInstrucao;
    @FXML private Label     labelFeedback;
    @FXML private Label     labelTentativas;

    @FXML private ImageView imgPersonagem;

    @FXML private Button btnSimbolo0;
    @FXML private Button btnSimbolo1;
    @FXML private Button btnSimbolo2;
    @FXML private Button btnSimbolo3;

    @FXML private Button btnPular;

    private static final int TAMANHO_SEQUENCIA  = 4;
    private static final int MAX_TENTATIVAS     = 3;
    private static final int STAMINA_RECOMPENSA = 40;
    private static final int STAMINA_CONSOLACAO = 10;

    private static final double INTERVALO_FLASH_ON  = 1.0;
    private static final double INTERVALO_FLASH_OFF = 0.5;
    private static final double PAUSA_INICIAL       = 0.8;

    private static final String[] NOMES_SIMBOLOS = { "Sangue", "Morte", "Energia", "Conhecimento" };

    private static final String[] ESTILOS_DESTAQUE = {
        "btn-descanso-sangue",
        "btn-descanso-morte",
        "btn-descanso-energia",
        "btn-descanso-conhecimento"
    };

    private Personagem    jogador;
    private List<Integer> sequencia            = new ArrayList<>();
    private List<Integer> inputJogador         = new ArrayList<>();
    private int           tentativasRestantes  = MAX_TENTATIVAS;
    private boolean       exibindoSequencia    = false;
    private boolean       finalizando          = false;

    private static final Random RAND = new Random();

    @FXML
    public void initialize() {
        jogador = ScreenManager.getInstance().getPersonagemAtivo();

        SpriteManager.getInstance().setSprite(imgPersonagem, jogador.getImagemPath());
        SpriteManager.getInstance().animarIdle(imgPersonagem);

        labelNomePersonagem.setText(jogador.getNome());
        atualizarHUD();

        setBotoesSimbolosAtivos(false);
        btnPular.setDisable(false);
        labelInstrucao.setText("Respire fundo, Agente. Observe os símbolos...");
        labelFeedback.setText("");
        labelTentativas.setText("Tentativas: " + tentativasRestantes + " / " + MAX_TENTATIVAS);

        PauseTransition pausa = new PauseTransition(Duration.seconds(PAUSA_INICIAL));
        pausa.setOnFinished(e -> iniciarRodada());
        pausa.play();
    }

    private void iniciarRodada() {
        sequencia.clear();
        inputJogador.clear();
        gerarSequencia();
        labelFeedback.setText("");
        labelTentativas.setText("Tentativas: " + tentativasRestantes + " / " + MAX_TENTATIVAS);
        labelInstrucao.setText("Memorize a sequência:");
        setBotoesSimbolosAtivos(false);
        btnPular.setDisable(true);
        animarSequencia();
    }

    private void gerarSequencia() {
        for (int i = 0; i < TAMANHO_SEQUENCIA; i++) {
            sequencia.add(RAND.nextInt(4));
        }
    }

    private void animarSequencia() {
        exibindoSequencia = true;
        Button[] botoes = { btnSimbolo0, btnSimbolo1, btnSimbolo2, btnSimbolo3 };

        Timeline tl = new Timeline();
        double cursor = PAUSA_INICIAL;

        for (int i = 0; i < sequencia.size(); i++) {
            final int idx = sequencia.get(i);

            final double tOn  = cursor;
            final double tOff = cursor + INTERVALO_FLASH_ON;
            cursor = tOff + INTERVALO_FLASH_OFF;

            KeyFrame kfOn = new KeyFrame(Duration.seconds(tOn), e -> {
                for (Button b : botoes) {
                    b.getStyleClass().removeAll(
                        "btn-descanso-sangue", "btn-descanso-morte",
                        "btn-descanso-energia", "btn-descanso-conhecimento"
                    );
                }
                botoes[idx].getStyleClass().add(ESTILOS_DESTAQUE[idx]);
                labelInstrucao.setText("▶  " + NOMES_SIMBOLOS[idx]);
            });

            KeyFrame kfOff = new KeyFrame(Duration.seconds(tOff), e -> {
                botoes[idx].getStyleClass().removeAll(
                    "btn-descanso-sangue", "btn-descanso-morte",
                    "btn-descanso-energia", "btn-descanso-conhecimento"
                );
                labelInstrucao.setText("...");
            });

            tl.getKeyFrames().addAll(kfOn, kfOff);
        }

        final double tempoFim = cursor;
        KeyFrame kfFim = new KeyFrame(Duration.seconds(tempoFim), e -> {
            exibindoSequencia = false;
            labelInstrucao.setText("Agora! Repita a sequência:");
            setBotoesSimbolosAtivos(true);
            btnPular.setDisable(false);
        });
        tl.getKeyFrames().add(kfFim);
        tl.play();
    }

    @FXML private void onSimbolo0() { registrarInput(0); }
    @FXML private void onSimbolo1() { registrarInput(1); }
    @FXML private void onSimbolo2() { registrarInput(2); }
    @FXML private void onSimbolo3() { registrarInput(3); }

    private void registrarInput(int simbolo) {
        if (exibindoSequencia || finalizando) return;

        inputJogador.add(simbolo);
        int pos = inputJogador.size() - 1;

        if (simbolo != sequencia.get(pos)) {
            tentativasRestantes--;
            setBotoesSimbolosAtivos(false);
            btnPular.setDisable(true);

            if (tentativasRestantes <= 0) {
                labelFeedback.setText("❌ Sequência errada! Você está exausto demais...");
                labelInstrucao.setText("Descansando um pouco mesmo assim.");
                labelTentativas.setText("Sem tentativas restantes.");
                finalizando = true;
                PauseTransition p = new PauseTransition(Duration.seconds(2));
                p.setOnFinished(e -> finalizarDescanso(STAMINA_CONSOLACAO));
                p.play();
            } else {
                labelFeedback.setText("❌ Errou! " + tentativasRestantes
                    + " tentativa" + (tentativasRestantes == 1 ? "" : "s") + " restante"
                    + (tentativasRestantes == 1 ? "" : "s") + ".");
                labelTentativas.setText("Tentativas: " + tentativasRestantes + " / " + MAX_TENTATIVAS);
                PauseTransition p = new PauseTransition(Duration.seconds(1.8));
                p.setOnFinished(e -> {
                    if (!finalizando) {
                        inputJogador.clear();
                        labelFeedback.setText("");
                        labelInstrucao.setText("Observe novamente:");
                        animarSequencia();
                    }
                });
                p.play();
            }
            return;
        }

        labelFeedback.setText("✔ " + NOMES_SIMBOLOS[simbolo]);

        if (inputJogador.size() == sequencia.size()) {
            setBotoesSimbolosAtivos(false);
            btnPular.setDisable(true);
            labelFeedback.setText("✅ Sequência correta!");
            labelInstrucao.setText("Você se concentrou e recuperou as forças!");
            finalizando = true;
            PauseTransition p = new PauseTransition(Duration.seconds(1.8));
            p.setOnFinished(e -> finalizarDescanso(STAMINA_RECOMPENSA));
            p.play();
        }
    }

    @FXML
    private void onPular() {
        if (exibindoSequencia || finalizando) return;
        finalizando = true;
        setBotoesSimbolosAtivos(false);
        btnPular.setDisable(true);
        labelFeedback.setText("Você pulou o descanso.");
        labelInstrucao.setText("Voltando à missão...");
        PauseTransition p = new PauseTransition(Duration.seconds(1));
        p.setOnFinished(e -> finalizarDescanso(0));
        p.play();
    }

    private void finalizarDescanso(int staminaRecuperada) {
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