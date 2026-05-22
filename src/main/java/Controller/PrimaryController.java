package Controller;



import Util.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class PrimaryController {

    @FXML
    private void onJogar() {
        ScreenManager.getInstance().ir(ScreenManager.TELA_JOGAR);
    }

    @FXML
    private void onRanking() {
        ScreenManager.getInstance().ir(ScreenManager.TELA_RANKING);
    }

    @FXML
    private void onCreditos() {
        ScreenManager.getInstance().ir(ScreenManager.TELA_CREDITOS);
    }

    @FXML
    private void onEncerrar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            "Deseja realmente sair?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Encerrar");
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                Stage stage = ScreenManager.getInstance().getStage();
                stage.close();
            }
        });
    }
}