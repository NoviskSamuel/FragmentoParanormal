package Controller;

import Model.Personagem;
import Util.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import Graphics.SpriteManager;

public class Confirmacao {

    @FXML private Label labelMensagem;
    @FXML private ImageView imgPersonagem;

    @FXML
    public void initialize() {
        Personagem p = ScreenManager.getInstance().getPersonagemAtivo();
        if (p != null) {
            labelMensagem.setText(
                "Agente " + p.getNome() + ",\na Ordem te chamou para uma missão."
            );
            SpriteManager.getInstance().setSprite(imgPersonagem, p.getImagemPath());
            SpriteManager.getInstance().animarIdle(imgPersonagem);
        }
    }

    @FXML
    private void onAceitar() {
        ScreenManager.getInstance().ir(ScreenManager.TELA_MISSOES);
    }

    @FXML
    private void onRecusar() {
        // Recusar → Créditos (conforme spec)
        ScreenManager.getInstance().irSemHistorico(ScreenManager.TELA_CREDITOS);
    }
}