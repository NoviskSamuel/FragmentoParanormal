package Default;

import Dao.ConexaoDB;
import Util.ScreenManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        ScreenManager.getInstance().init(stage);
    }

    @Override
    public void stop() {
        ConexaoDB.fecharConexao();
        ScreenManager.getInstance().limparSessao();
    }

    public static void main(String[] args) {
        launch();
    }
}