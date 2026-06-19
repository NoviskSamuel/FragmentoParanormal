package Util;

import Model.Batalha;
import Model.Missao;
import Model.Personagem;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
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
    private Missao      missaoAtiva;

    private Batalha batalhaSalva;
    private int     inimigosMortosSalvos;
    private int     moedasSessaoSalvas;
    private String  narrativaSalva;
    private boolean estadoMissaoSalvo = false;

    public static final String TELA_INICIAL      = "primary";
    public static final String TELA_JOGAR        = "jogar";        // Tela de escolha (Novo vs Existente)
    public static final String TELA_NOVO_JOGADOR = "novo_jogador"; // Tela de criação
    public static final String TELA_CONFIRMACAO  = "confirmacao";
    public static final String TELA_MISSOES      = "missoes";      // Lista de missões (Jogador existente vai para cá)
    public static final String TELA_MISSAO       = "missao";       // A tela do jogo/combate em si
    public static final String TELA_INVENTARIO   = "inventario";
    public static final String TELA_LEVEL_UP     = "level_up";
    public static final String TELA_RANKING      = "ranking";
    public static final String TELA_CREDITOS     = "creditos";
    public static final String TELA_DESCANSO     = "descanso";
    
    public void init(Stage stage) throws IOException {
        this.stage = stage;
        Parent root = carregar(TELA_INICIAL);
        this.scene = new Scene(root, 960, 640);

        // Tenta carregar o estilo de forma segura
        URL css = ScreenManager.class.getResource("/fragmentoparanormal/estilo.css");
        if (css == null) {
            css = ScreenManager.class.getResource("/estilo.css");
        }
        
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("Aviso: estilo.css não encontrado. Iniciando sem estilos visuais.");
        }

        stage.setScene(scene);
        stage.setTitle("Fragmento Paranormal");
        stage.setResizable(false);
        stage.show();
    }

    private Parent carregar(String fxml) throws IOException {
        String nomeArquivo = fxml + ".fxml";
        URL url = null;

        // Tentativa 1: Procurar dentro do pacote 'fragmentoparanormal'
        url = ScreenManager.class.getResource("/fragmentoparanormal/" + nomeArquivo);

        // Tentativa 2: Procurar direto na raiz de resources
        if (url == null) {
            url = ScreenManager.class.getResource("/" + nomeArquivo);
        }

        // Tentativa 3: Forçar via ClassLoader da Thread corrente
        if (url == null) {
            url = Thread.currentThread().getContextClassLoader().getResource(nomeArquivo);
        }

        if (url == null) {
            throw new IOException("Erro crítico: O arquivo [" + nomeArquivo + "] não foi encontrado em nenhuma pasta de recursos.\n" +
                    "Verifique se o nome está correto na sua pasta src/main/resources.");
        }

        FXMLLoader loader = new FXMLLoader(url);
        return loader.load();
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

    private void trocar(String fxml) {
        try {
            scene.setRoot(carregar(fxml));
        } catch (IOException e) {
            throw new RuntimeException(
                "Falha ao carregar tela '" + fxml + "'. Verifique se o arquivo FXML existe.", e);
        }
    }

    public Personagem getPersonagemAtivo()             { return personagemAtivo; }
    public void       setPersonagemAtivo(Personagem p) { this.personagemAtivo = p; }

    public Missao getMissaoAtiva()                     { return missaoAtiva; }
    public void   setMissaoAtiva(Missao m)             { this.missaoAtiva = m; }

    public void salvarEstadoMissao(Batalha batalha, int inimigosMortos, int moedasSessao, String narrativa) {
        this.batalhaSalva         = batalha;
        this.inimigosMortosSalvos = inimigosMortos;
        this.moedasSessaoSalvas   = moedasSessao;
        this.narrativaSalva       = narrativa;
        this.estadoMissaoSalvo    = true;
    }

    public boolean temEstadoMissaoSalvo()  { return estadoMissaoSalvo; }
    public Batalha getBatalhaSalva()       { return batalhaSalva; }
    public int     getInimigosMortosSalvos() { return inimigosMortosSalvos; }
    public int     getMoedasSessaoSalvas()   { return moedasSessaoSalvas; }
    public String  getNarrativaSalva()       { return narrativaSalva; }

    public void limparEstadoMissao() {
        this.batalhaSalva         = null;
        this.inimigosMortosSalvos = 0;
        this.moedasSessaoSalvas   = 0;
        this.narrativaSalva       = null;
        this.estadoMissaoSalvo    = false;
    }

    public Stage  getStage()
    {
        return stage; 
    }

    public void limparSessao() {
        personagemAtivo = null;
        missaoAtiva     = null;
        historico.clear();
        limparEstadoMissao();
    }
    
    public String getCaminhoCenarioAtual() {
        Missao missaoAtiva = getMissaoAtiva();
        if (missaoAtiva == null) {
            return "/fragmentoparanormal/images/ui/placeholder.png"; 
        }

        int sala = missaoAtiva.getSalaAtual();
        int totalSalas = missaoAtiva.getTotalSalas();

        // Se o ID da missão for 1
        if (missaoAtiva.getId() == 1) {
            if (sala >= totalSalas) {
                return "/Assets/Cenarios/Missao1_salaFinal.jpg";
            }
            
            return switch (sala) {
                case 0, 1 -> "/Assets/Cenarios/Missao1_sala1.jpg";
                case 2    -> "/Assets/Cenarios/Missao1_sala2.jpg";
                case 3    -> "/Assets/Cenarios/Missao1_sala3.jpg";
                case 4    -> "/Assets/Cenarios/Missao1_sala4.jpg";
                default   -> "/Assets/Cenarios/Missao1_sala4.jpg";
            };
        }

        // Fallback genérico para outras missões que você criar depois
        return "/fragmentoparanormal/images/ui/placeholder.png";
    } // <- Fecha corretamente o getCaminhoCenarioAtual
        
    public void abrirInventarioComoJanela() {
        try {
            URL url = ScreenManager.class.getResource("/fragmentoparanormal/inventario.fxml");
            if (url == null) throw new IOException("inventario.fxml não encontrado.");
            Parent root = new FXMLLoader(url).load();

            Stage janelaInventario = new Stage();
            janelaInventario.setTitle("Inventário — Fragmento Paranormal");
            janelaInventario.initOwner(stage);
            janelaInventario.initModality(Modality.WINDOW_MODAL);

            Scene cena = new Scene(root, 960, 640);
            if (scene != null && !scene.getStylesheets().isEmpty()) {
                cena.getStylesheets().addAll(scene.getStylesheets());
            }
            janelaInventario.setScene(cena);
            janelaInventario.setResizable(false);
            janelaInventario.showAndWait(); // bloqueia a janela principal até fechar
        } catch (IOException e) {
            throw new RuntimeException("Falha ao abrir inventário em janela própria.", e);
        }
    }
}