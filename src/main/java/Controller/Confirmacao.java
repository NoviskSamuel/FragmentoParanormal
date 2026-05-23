package Controller;

import Graphics.SpriteManager;
import Model.Personagem;
import Util.ScreenManager;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class Confirmacao {

    @FXML private Label     labelMensagem;
    @FXML private Label     labelClasse;
    @FXML private Label     labelNivel;
    @FXML private ImageView imgPersonagem;

    private ScaleTransition idleAnim;

    @FXML
    public void initialize() {
        Personagem p = ScreenManager.getInstance().getPersonagemAtivo();
        if (p != null) {
            labelMensagem.setText(
                "Agente " + p.getNome() + ",\na Ordem te chamou para uma missão.\n\nVocê aceita?"
            );
            labelClasse.setText(p.getClasse().getNome() + " — " + p.getClasse().getDescricao());
            labelNivel.setText("Nível " + p.getNivel());
            SpriteManager.getInstance().setSprite(imgPersonagem, p.getImagemPath());
            idleAnim = SpriteManager.getInstance().animarIdle(imgPersonagem);
        }
    }

    @FXML
    private void onAceitar() {
        pararIdle();
        ScreenManager.getInstance().ir(ScreenManager.TELA_MISSOES);
    }

    @FXML
    private void onRecusar() {
        pararIdle();
        ScreenManager.getInstance().irSemHistorico(ScreenManager.TELA_CREDITOS);
    }

    private void pararIdle() {
        if (idleAnim != null) idleAnim.stop();
    }
}