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

    @FXML
    public void initialize() {
        jogador = ScreenManager.getInstance().getPersonagemAtivo();
        if (jogador == null) return;
        labelNivel.setText("Nível " + jogador.getNivel());
        labelForca.setText(String.valueOf(jogador.getForca()));
        labelPoder.setText(String.valueOf(jogador.getPoderParanormal()));
        labelInvestigacao.setText(String.valueOf(jogador.getInvestigacao()));
        labelVida.setText(String.valueOf(jogador.getVidaMaxima()));
        atualizarPontos();
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
        labelForca.setText(String.valueOf(jogador.getForca()));
        labelPoder.setText(String.valueOf(jogador.getPoderParanormal()));
        labelInvestigacao.setText(String.valueOf(jogador.getInvestigacao()));
        labelVida.setText(String.valueOf(jogador.getVidaMaxima()));
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