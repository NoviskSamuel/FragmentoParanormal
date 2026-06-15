package Controller;

import Dao.PersonagemDAO;
import Model.Personagem;
import Util.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.SQLException;

public class LevelUpController {
    @FXML private Label labelNivel;
    @FXML private Label labelForca;
    @FXML private Label labelPoder;
    @FXML private Label labelInvestigacao;
    @FXML private Label labelVida;
    @FXML private Label labelPontosRestantes;

    private Personagem jogador;
    private int pontosRestantes = 3;
    private int investidoForca        = 0;
    private int investidoPoder        = 0;
    private int investidoInvestigacao = 0;
    private int investidoVida         = 0;

    @FXML
    public void initialize() {
        jogador = ScreenManager.getInstance().getPersonagemAtivo();
        if (jogador == null) return;
        labelNivel.setText("Nível " + jogador.getNivel());
        atualizarLabels();
        atualizarPontos();
    }

    private void atualizarLabels() {
        labelForca.setText(String.valueOf(jogador.getForca()));
        labelPoder.setText(String.valueOf(jogador.getPoderParanormal()));
        labelInvestigacao.setText(String.valueOf(jogador.getInvestigacao()));
        labelVida.setText(String.valueOf(jogador.getVidaMaxima()));
    }

    private void atualizarPontos() {
        labelPontosRestantes.setText("Pontos restantes: " + pontosRestantes);
    }

    @FXML private void onUpForca()         { gastar("forca"); }
    @FXML private void onUpPoder()         { gastar("poder"); }
    @FXML private void onUpInvestigacao()  { gastar("investigacao"); }
    @FXML private void onUpVida()          { gastar("vida"); }

    private void gastar(String atributo) {
        if (pontosRestantes <= 0 || jogador == null) return;
        jogador.subirAtributo(atributo, 1);
        pontosRestantes--;
        switch (atributo) {
            case "forca"        -> investidoForca++;
            case "poder"        -> investidoPoder++;
            case "investigacao" -> investidoInvestigacao++;
            case "vida"         -> investidoVida++;
        }
        atualizarLabels();
        atualizarPontos();
    }

    @FXML private void onDownForca()        { devolver("forca"); }
    @FXML private void onDownPoder()        { devolver("poder"); }
    @FXML private void onDownInvestigacao() { devolver("investigacao"); }
    @FXML private void onDownVida()         { devolver("vida"); }

    private void devolver(String atributo) {
        if (jogador == null) return;
        boolean podeDevolver = switch (atributo) {
            case "forca"        -> investidoForca        > 0;
            case "poder"        -> investidoPoder        > 0;
            case "investigacao" -> investidoInvestigacao > 0;
            case "vida"         -> investidoVida         > 0;
            default             -> false;
        };
        
        if (!podeDevolver) return;

        switch (atributo) {
            case "forca"        -> { jogador.setForca(jogador.getForca() - 1); investidoForca--; }
            case "poder"        -> { jogador.setPoderParanormal(jogador.getPoderParanormal() - 1); investidoPoder--; }
            case "investigacao" -> { jogador.setInvestigacao(jogador.getInvestigacao() - 1); investidoInvestigacao--; }
            case "vida"         -> {
                jogador.setVidaMaxima(jogador.getVidaMaxima() - 5);
                jogador.setVidaAtual(Math.min(jogador.getVidaAtual(), jogador.getVidaMaxima()));
                investidoVida--;
            }
        }
        pontosRestantes++;
        atualizarLabels();
        atualizarPontos();
    }

    @FXML
    private void onConfirmar() {
        if (jogador != null) {
            try { new PersonagemDAO().atualizar(jogador); } catch (SQLException ignored) {}
        }
        ScreenManager.getInstance().irSemHistorico(ScreenManager.TELA_MISSOES);
    }
}