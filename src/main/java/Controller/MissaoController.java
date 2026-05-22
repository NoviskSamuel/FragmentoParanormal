package Controller;

import Dao.MissaoDAO;
import Dao.PersonagemDAO;
import Graphics.SpriteManager;
import Model.Inimigo;
import Model.Item;
import Model.Missao;
import Model.Personagem;
import Util.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.Random;

public class MissaoController {

    // ── FXML ───────────────────────────────────────────────────────────────
    @FXML private Label labelTituloMissao;
    @FXML private Label labelObjetivo;
    @FXML private Label labelSala;
    @FXML private Label labelVida;
    @FXML private Label labelNarrative;
    @FXML private ImageView imgPersonagem;
    @FXML private ImageView imgJumpscare;   // sobreposto, invisible por padrão
    @FXML private HBox painelAcoes;         // botões de ação normal
    @FXML private HBox painelBatalha;       // botões de batalha (hidden inicialmente)
    @FXML private VBox painelItem;          // aparece quando encontra item
    @FXML private Label labelItemEncontrado;
    @FXML private Label labelFragmento;     // carta do fragmento

    // ── Estado ─────────────────────────────────────────────────────────────
    private Personagem jogador;
    private Missao missao;
    private Inimigo inimigoAtual;
    private final Random rand = new Random();

    // ── Init ───────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        jogador = ScreenManager.getInstance().getPersonagemAtivo();
        missao  = ScreenManager.getInstance().getMissaoAtiva();

        labelTituloMissao.setText(missao.getTitulo());
        atualizarHUD();
        painelBatalha.setVisible(false);
        painelItem.setVisible(false);
        imgJumpscare.setVisible(false);

        SpriteManager.getInstance().setSprite(imgPersonagem, jogador.getImagemPath());
        SpriteManager.getInstance().animarIdle(imgPersonagem);

        labelNarrative.setText("Você entrou na missão. Avance com cuidado, Agente.");
    }

    // ── HUD ────────────────────────────────────────────────────────────────
    private void atualizarHUD() {
        labelObjetivo.setText(
            missao.getObjetivo() + ": " + missao.getProgressoAtual() +
            "/" + missao.getTotalObjetivo()
        );
        labelSala.setText("Sala " + missao.getSalaAtual() + "/" + missao.getTotalSalas());
        labelVida.setText("❤ " + jogador.getVidaAtual() + "/" + jogador.getVidaMaxima());
    }

    // ── Ações do jogador ───────────────────────────────────────────────────

    @FXML
    private void onAvancar() {
        missao.setSalaAtual(missao.getSalaAtual() + 1);
        atualizarHUD();
        gerarEvento();
    }

    @FXML
    private void onInvestigar() {
        int chance = 20 + jogador.getInvestigacao();
        if (rand.nextInt(100) < chance) {
            // Encontrou item
            Item item = gerarItemAleatorio();
            jogador.getInventario().adicionarItem(item);
            labelItemEncontrado.setText("Encontrou: " + item.getNome() + "\n" + item.getDescricao());
            painelItem.setVisible(true);
            labelNarrative.setText("Sua investigação revelou algo...");

            // Se é fragmento, atualiza progresso
            if ("FRAGMENTO".equalsIgnoreCase(item.getTipo())) {
                missao.setProgressoAtual(missao.getProgressoAtual() + 1);
                labelFragmento.setText("📜 Fragmento coletado! (" +
                    missao.getProgressoAtual() + "/" + missao.getTotalObjetivo() + ")");
                verificarConclusao();
            }
        } else {
            labelNarrative.setText("Você investigou a sala mas não encontrou nada além de sombras.");
        }
        atualizarHUD();
    }

    @FXML
    private void onFecharItem() {
        painelItem.setVisible(false);
    }

    @FXML
    private void onFugir() {
        missao.setFugiu(true);
        missao.setVezesRetornou(missao.getVezesRetornou() + 1);
        salvarProgresso();
        labelNarrative.setText("Você fugiu. O progresso foi salvo, mas os inimigos ficaram mais fortes...");
        ScreenManager.getInstance().irSemHistorico(ScreenManager.TELA_MISSOES);
    }

    @FXML
    private void onInventario() {
        ScreenManager.getInstance().ir(ScreenManager.TELA_INVENTARIO);
    }

    // ── Batalha ───────────────────────────────────────────────────────────

    private void iniciarBatalha(Inimigo inimigo) {
        this.inimigoAtual = inimigo;
        ScreenManager.getInstance().setMissaoAtiva(missao);
        // Passa o inimigo via contexto e navega
        // (Usamos uma variável temporária estática simples)
        BatalhaController.setInimigoAtual(inimigo);
        ScreenManager.getInstance().ir(ScreenManager.TELA_BATALHA);
    }

    // ── Geração de eventos ────────────────────────────────────────────────

    private void gerarEvento() {
        int rolagem = rand.nextInt(100);

        if (rolagem < 50) {
            // Inimigo!
            Inimigo inimigo = gerarInimigoAleatorio();
            // Escalar se voltou após fuga
            if (missao.isFugiu() && missao.getVezesRetornou() > 0) {
                inimigo.escalar(1.0 + 0.2 * missao.getVezesRetornou());
            }
            // JUMPSCARE primeiro, depois batalha
            String jumpPath = inimigo.getImagemJumpscare() != null
                ? inimigo.getImagemJumpscare()
                : inimigo.getImagemPath();
            SpriteManager.getInstance().exibirJumpscare(imgJumpscare, jumpPath,
                () -> iniciarBatalha(inimigo));
        } else if (rolagem < 75) {
            labelNarrative.setText("A sala está vazia. Apenas o vento uiva nas paredes.");
        } else {
            // Item aleatório
            onInvestigar();
        }
    }

    private Inimigo gerarInimigoAleatorio() {
        String[] nomes = {"Sombra Errante", "Espírito Maligno", "Criatura das Trevas", "Fantasma Raivoso"};
        String[] imgs  = {"/images/inimigos/sombra.png", "/images/inimigos/espirito.png",
                          "/images/inimigos/criatura.png", "/images/inimigos/fantasma.png"};
        int i = rand.nextInt(nomes.length);
        int fator = missao.getSalaAtual();
               Inimigo in = new Inimigo(nomes[i],
            Util.Elemento.SOMBRA,
            fator, 8 + fator * 2, 30 + fator * 10, 20 + fator * 5);

        in.setImagemPath(imgs[i]);
        in.setImagemJumpscare(imgs[i].replace(".png", "_jump.png"));
        return in;
    }

    private Item gerarItemAleatorio() {
        String[][] itens = {
            {"Faca Enferrujada", "ARMA", "5", "Uma faca velha mas ainda cortante."},
            {"Fragmento do Diário", "FRAGMENTO", "0", "Uma página rasgada do diário perdido."},
            {"Poção de Ervas", "POCAO", "30", "Restaura 30 pontos de vida."},
            {"Amuleto Sombrio", "RITUAL", "15", "Amplifica poder paranormal."}
        };
        int i = rand.nextInt(itens.length);
        return new Item(itens[i][0], itens[i][1],
            Integer.parseInt(itens[i][2]), itens[i][3]);
    }

    private void verificarConclusao() {
        if (missao.getProgressoAtual() >= missao.getTotalObjetivo()) {
            missao.setConcluida(true);
            salvarProgresso();
            // Level up: ganhar XP
            jogador.ganharXP(150 + missao.getNivelMinimo() * 50);
            salvarPersonagem();
            labelNarrative.setText("🏆 MISSÃO CONCLUÍDA! Você coletou todos os fragmentos!");
            // Navega para tela de level up
            ScreenManager.getInstance().ir(ScreenManager.TELA_LEVEL_UP);
        }
    }

    private void salvarProgresso() {
        new MissaoDAO().atualizarProgresso(missao);
    }

    private void salvarPersonagem() {
        try {
            new PersonagemDAO().atualizar(jogador);
        } catch (SQLException e) {
            System.err.println("Erro ao salvar personagem: " + e.getMessage());
        }
    }
}