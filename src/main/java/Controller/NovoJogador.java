package Controller;

import Dao.PersonagemDAO;
import Util.Classe;
import Util.Elemento;
import Util.Genero;
import Graphics.SpriteManager;
import Model.Personagem;
import Util.ScreenManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.sql.SQLException;

public class NovoJogador {

    // ── FXML ───────────────────────────────────────────────────────────────
    @FXML private TextField campNome;
    @FXML private ToggleGroup grupoClasse;
    @FXML private RadioButton rbEspecialista;
    @FXML private RadioButton rbCombatente;
    @FXML private RadioButton rbOcultista;
    @FXML private ToggleGroup grupoGenero;
    @FXML private RadioButton rbMasculino;
    @FXML private RadioButton rbFeminino;
    @FXML private ComboBox<String> comboElemento;
    @FXML private ImageView imgPreview;
    @FXML private Label labelDescClasse;
    @FXML private Label labelErro;

    // ── Init ───────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        comboElemento.getItems().addAll(
            "FOGO", "AGUA", "TERRA", "AR", "TREVAS", "LUZ"
        );
        comboElemento.getSelectionModel().selectFirst();

        // Atualiza preview sempre que classe ou gênero mudar
        grupoClasse.selectedToggleProperty().addListener((obs, o, n) -> atualizarPreview());
        grupoGenero.selectedToggleProperty().addListener((obs, o, n) -> atualizarPreview());

        atualizarPreview();
    }

    // ── Ações ─────────────────────────────────────────────────────────────

    @FXML
    private void onConfirmar() {
        String nome = campNome.getText().trim();
        if (nome.isBlank()) {
            labelErro.setText("Digite um nome para o agente.");
            return;
        }
        if (grupoClasse.getSelectedToggle() == null) {
            labelErro.setText("Escolha uma classe.");
            return;
        }
        if (grupoGenero.getSelectedToggle() == null) {
            labelErro.setText("Escolha um gênero.");
            return;
        }

        Classe classe  = classeEscolhida();
        Genero genero  = generoEscolhido();
        Elemento elem  = Elemento.valueOf(comboElemento.getValue());

        Personagem novo = new Personagem(nome, classe, genero, elem);

        try {
            PersonagemDAO dao = new PersonagemDAO();
            dao.salvar(novo);
            ScreenManager.getInstance().setPersonagemAtivo(novo);
            ScreenManager.getInstance().ir(ScreenManager.TELA_CONFIRMACAO);
        } catch (SQLException e) {
            labelErro.setText("Erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    private void onSair() {
        ScreenManager.getInstance().voltar();
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private void atualizarPreview() {
        Classe c = classeEscolhida();
        Genero g = generoEscolhido();
        if (c == null || g == null) return;

        // Monta caminho igual ao Personagem.resolverImagem()
        String path = switch (c) {
            case ESPECIALISTA -> g == Genero.MASCULINO
                ? "/images/personagens/arthur.png"
                : "/images/personagens/erin.png";
            case COMBATENTE -> g == Genero.MASCULINO
                ? "/images/personagens/dominic.png"
                : "/images/personagens/carina.png";
            case OCULTISTA -> g == Genero.MASCULINO
                ? "/images/personagens/dante.png"
                : "/images/personagens/agatha.png";
        };

        SpriteManager.getInstance().setSprite(imgPreview, path);

        labelDescClasse.setText(c.getDescricao());
    }

    private Classe classeEscolhida() {
        if (rbEspecialista.isSelected()) return Classe.ESPECIALISTA;
        if (rbCombatente.isSelected())   return Classe.COMBATENTE;
        if (rbOcultista.isSelected())    return Classe.OCULTISTA;
        return null;
    }

    private Genero generoEscolhido() {
        if (rbMasculino.isSelected()) return Genero.MASCULINO;
        if (rbFeminino.isSelected())  return Genero.FEMININO;
        return null;
    }
}