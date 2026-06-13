package Controller;

import Util.ScreenManager;
import javafx.fxml.FXML;

public class CreditosController {

    @FXML private void onVoltar() {
        ScreenManager.getInstance().irSemHistorico(ScreenManager.TELA_INICIAL);
    }
}