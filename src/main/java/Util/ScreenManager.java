package Util;

import Model.Personagem;
import Model.Missao;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Gerenciador central de telas do Fragmento Paranormal.
 * Singleton. Controla navegação, contexto de jogo e histórico de telas.
 */
public class ScreenManager {

    // ── Singleton ──────────────────────────────────────────────────────────
    private static ScreenManager instance;

    public static ScreenManager getInstance() {
        if (instance == null) instance = new ScreenManager();
        return instance;
    }

    private ScreenManager() {}

    // ── Estado ─────────────────────────────────────────────────────────────
    private Stage stage;
    private Scene scene;
    private final Deque<String> pilha = new ArrayDeque<>();

    /** Personagem em uso na sessão atual */
    private Personagem personagemAtivo;

    /** Missão em curso */
    private Missao missaoAtiva;

    // ── Init ───────────────────────────────────────────────────────────────
    public void init(Stage stage) throws IOException {
        this.stage = stage;
        Parent root = carregar("primary");
        scene = new Scene(root, 900, 650);
        scene.getStylesheets().add(
            getClass().getResource("/css/estilo.css").toExternalForm()
        );
        stage.setScene(scene);
        stage.setTitle("Fragmento Paranormal");
        stage.setResizable(false);
        stage.show();
    }

    // ── Navegação pública ──────────────────────────────────────────────────

    /** Vai para uma tela e empilha a anterior para poder voltar. */
    public void ir(String fxml) {
        pilha.push(fxml);
        trocar(fxml);
    }

    /** Navega sem guardar histórico (ex.: tela inicial após créditos). */
    public void irSemHistorico(String fxml) {
        pilha.clear();
        trocar(fxml);
    }

    /** Volta para a tela anterior (se houver). */
    public void voltar() {
        if (!pilha.isEmpty()) {
            trocar(pilha.pop());
        }
    }

    // ── Nomes de telas (constantes estáticas para evitar typos) ───────────
    public static final String TELA_INICIAL        = "primary";
    public static final String TELA_RANKING        = "ranking";
    public static final String TELA_CREDITOS       = "creditos";
    public static final String TELA_JOGAR          = "jogar";
    public static final String TELA_NOVO_JOGADOR   = "novo_jogador";
    public static final String TELA_CONFIRMACAO    = "confirmacao";
    public static final String TELA_MISSOES        = "missoes";
    public static final String TELA_MISSAO         = "missao";
    public static final String TELA_BATALHA        = "batalha";
    public static final String TELA_INVENTARIO     = "inventario";
    public static final String TELA_LEVEL_UP       = "level_up";

    // ── Contexto de jogo ───────────────────────────────────────────────────
    public Personagem getPersonagemAtivo() { return personagemAtivo; }
    public void setPersonagemAtivo(Personagem p) { this.personagemAtivo = p; }

    public Missao getMissaoAtiva() { return missaoAtiva; }
    public void setMissaoAtiva(Missao m) { this.missaoAtiva = m; }

    public Stage getStage() { return stage; }

    // ── Interno ────────────────────────────────────────────────────────────
    private void trocar(String fxml) {
        try {
            Parent root = carregar(fxml);
            scene.setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar tela: " + fxml, e);
        }
    }

    private Parent carregar(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource(
                "/com/mycompany/fragmentoparanormal/" + fxml + ".fxml"
            )
        );
        return loader.load();
    }
}