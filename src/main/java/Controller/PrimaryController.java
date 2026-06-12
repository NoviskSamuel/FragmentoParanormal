package Controller;

import Util.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class PrimaryController {

    @FXML private void onJogar()    { ScreenManager.getInstance().ir(ScreenManager.TELA_JOGAR); }
    @FXML private void onRanking()  { ScreenManager.getInstance().ir(ScreenManager.TELA_RANKING); }
    @FXML private void onCreditos() { ScreenManager.getInstance().ir(ScreenManager.TELA_CREDITOS); }

    @FXML
    private void onEncerrar() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION,
            "Deseja realmente sair do Fragmento Paranormal?",
            ButtonType.YES, ButtonType.NO);
        alerta.setTitle("Encerrar");
        alerta.setHeaderText(null);
        alerta.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                ScreenManager.getInstance().getStage().close();
            }
        });
    }
}