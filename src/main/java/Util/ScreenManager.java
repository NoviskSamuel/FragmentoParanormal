package Util;

import Model.Missao;
import Model.Personagem;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;

public class ScreenManager {

    private static ScreenManager instance;

    public static ScreenManager getInstance() {
        if (instance == null) instance = new ScreenManager();
        return instance;
    }

    private ScreenManager() {}

    private Stage  stage;
    private Scene  scene;

    private final Deque<String> historico = new ArrayDeque<>();
    private static final int MAX_HISTORICO = 20;

    private Personagem personagemAtivo;
    private Missao     missaoAtiva;

    public static final String TELA_INICIAL      = "primary";
    public static final String TELA_JOGAR        = "jogar";
    public static final String TELA_NOVO_JOGADOR = "novo_jogador";
    public static final String TELA_CONFIRMACAO  = "confirmacao";
    public static final String TELA_MISSOES      = "missoes";
    public static final String TELA_MISSAO       = "missao";
    public static final String TELA_BATALHA      = "batalha";
    public static final String TELA_INVENTARIO   = "inventario";
    public static final String TELA_LEVEL_UP     = "level_up";
    public static final String TELA_RANKING      = "ranking";
    public static final String TELA_CREDITOS     = "creditos";

    public void init(Stage stage) throws IOException {
        this.stage = stage;
        Parent root = carregar(TELA_INICIAL);
        this.scene = new Scene(root, 960, 640);

        URL css = getClass().getResource("/com/mycompany/fragmentoparanormal/css/estilo.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Fragmento Paranormal");
        stage.setResizable(false);
        stage.show();
    }

    public void ir(String fxml) {
        if (scene == null) throw new IllegalStateException("ScreenManager não inicializado.");
        if (historico.size() >= MAX_HISTORICO) historico.pollLast();
        historico.push(fxml);
        trocar(fxml);
    }

    public void irSemHistorico(String fxml) {
        if (scene == null) throw new IllegalStateException("ScreenManager não inicializado.");
        historico.clear();
        trocar(fxml);
    }

    public void voltar() {
        if (!historico.isEmpty()) {
            historico.pop();
            String anterior = historico.isEmpty() ? TELA_INICIAL : historico.peek();
            trocar(anterior);
        } else {
            trocar(TELA_INICIAL);
        }
    }

    public Personagem getPersonagemAtivo()             { return personagemAtivo; }
    public void       setPersonagemAtivo(Personagem p) { this.personagemAtivo = p; }

    public Missao getMissaoAtiva()                     { return missaoAtiva; }
    public void   setMissaoAtiva(Missao m)             { this.missaoAtiva = m; }

    public Stage  getStage()                           { return stage; }

    public void limparSessao() {
        personagemAtivo = null;
        missaoAtiva     = null;
        historico.clear();
    }

    private void trocar(String fxml) {
        try {
            scene.setRoot(carregar(fxml));
        } catch (IOException e) {
            throw new RuntimeException(
                "Falha ao carregar tela '" + fxml + "'. Verifique se o arquivo FXML existe.", e);
        }
    }

    private Parent carregar(String fxml) throws IOException {
        String path = "/com/mycompany/fragmentoparanormal/" + fxml + ".fxml";
        URL url = getClass().getResource(path);
        if (url == null) {
            throw new IOException("FXML não encontrado: " + path);
        }
        FXMLLoader loader = new FXMLLoader(url);
        return loader.load();
    }
}